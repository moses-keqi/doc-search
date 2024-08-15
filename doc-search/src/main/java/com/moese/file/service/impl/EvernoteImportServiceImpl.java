package com.moese.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moese.file.entity.Doc;
import com.moese.file.entity.EvernoteImport;
import com.moese.file.entity.UploadFile;
import com.moese.file.evernote.EverNote;
import com.moese.file.evernote.EverNoteHelper;
import com.moese.file.exception.SystemException;
import com.moese.file.mapper.EvernoteImportMapper;
import com.moese.file.service.IDocService;
import com.moese.file.service.IEvernoteImportService;
import com.moese.file.service.IThreadPoolExecutorService;
import com.moese.file.service.IUploadFileService;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zxc
 * @since 2018-07-04
 */
@Service
public class EvernoteImportServiceImpl extends
    ServiceImpl<EvernoteImportMapper, EvernoteImport> implements IEvernoteImportService {

    @Autowired
    private IUploadFileService uploadFileService;
    @Autowired
    private IThreadPoolExecutorService executorService;
    @Autowired
    private IDocService docService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //@Transactional
    @Override
    public boolean importEverNote(MultipartFile file, Integer userId) {
        if (file != null) {
            UploadFile uploadFile = uploadFileService.saveFile(file, file.getOriginalFilename());
            if (uploadFile == null) {
                throw new SystemException("文件上传失败");
            } else {
                String fileId = uploadFile.getFileId();
                EvernoteImport evernoteImport = new EvernoteImport();
                evernoteImport.setEvernoteFileId(fileId);
                evernoteImport.setImportDate(new Date());
                evernoteImport.setUserId(userId);
                evernoteImport.setImportStatus(0);
                baseMapper.insert(evernoteImport);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    System.out.println(
                        objectMapper
                            .writeValueAsString(
                                baseMapper.selectById(evernoteImport.getEvernoteId())));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                executorService.execute(() -> importEverNoteSync(evernoteImport.getEvernoteId()));
                return true;
            }
        } else {
            throw new SystemException("文件不能为空");
        }
    }

    public Doc convertEverNoteToDoc(EverNote everNote, EvernoteImport evernoteImport) {
        if (everNote != null) {
            Doc doc = new Doc();
            doc.setDocTitle(everNote.getTitle());
            doc.setDocUserId(evernoteImport.getUserId());
            doc.setDocType("evernote");
            doc.setDocSize((long) everNote.getContent().getBytes().length);
            doc.setDocOpen(0);
            doc.setDocName(everNote.getTitle());
            doc.setDocModifyDate(new Date());
            doc.setDocDelete(0);
            doc.setDocCreateDate(everNote.getCreated());
            UploadFile uploadFile = uploadFileService
                .saveFile(everNote.getContent(), "xml", everNote.getTitle(), everNote.getTitle());
            doc.setDocFileId(uploadFile.getFileId());
            doc.setDocModifyDate(everNote.getUpdated());
            doc.setDocIndex(0);
            doc.setDocStatus(0);
            doc.setSource("evernote");
            doc.setSourceUrl(everNote.getSourceUrl());
            doc.setDocSha256(uploadFile.getSha256());
            doc.setDocConvert(1);
            if (docService.queryDocBySha256(doc.getDocSha256(), evernoteImport.getUserId())
                != null) {
                logger.info("笔记已导入");
                return null;
            } else {
                docService.getBaseMapper().insert(doc);
                executorService.execute(() -> docService.indexDocSync(doc));
                return doc;
            }

            //executorService.execute(() -> indexDocSync(doc));
            //executorService.singleExecute(()-> convertDocSync(doc));
        }
        return null;
    }

    //@Transactional
    @Override
    public void importEverNoteSync(Integer importId) {
        EvernoteImport evernoteImport = baseMapper.selectById(importId);
        if (evernoteImport == null) {
            logger.error("没有找到这条印象笔记的导入记录:{}", importId);
        } else {
            if (evernoteImport.getImportStatus() == EvernoteImport.IMPORT_WAIT) {
                evernoteImport.setImportStatus(EvernoteImport.IMPORT_ING);
                baseMapper.updateById(evernoteImport);
                String fileId = evernoteImport.getEvernoteFileId();
                UploadFile uploadFile = uploadFileService.getUploadFile(fileId);
                File everNoteFile = new File(uploadFile.getFilePath());
                if (everNoteFile.exists()) {

                    try {
                        List<EverNote> everNoteList = new ArrayList<>();
                        EverNoteHelper.parseEverNoteFile(everNoteFile, everNoteList::add);
                        if (everNoteList.isEmpty()) {
                            evernoteImport.setImportStatus(EvernoteImport.IMPORT_FAIL);
                            evernoteImport.setSuccessSize(0);
                            evernoteImport.setImportResult("导入失败,未从文件中解析到印象笔记");
                            baseMapper.updateById(evernoteImport);
                        } else {
                            int successSize = 0;
                            int failSize = 0;
                            for (EverNote everNote : everNoteList) {
                                Doc doc = convertEverNoteToDoc(everNote, evernoteImport);
                                if (doc == null) {
                                    failSize++;
                                } else {
                                    successSize++;
                                }
                            }
                            evernoteImport.setSuccessSize(successSize);
                            evernoteImport.setImportResult(String
                                .format("导入成功,其中成功%s条，失败%s条（如果有失败,原因可能为已经存在该笔记）", successSize,
                                    failSize));
                            evernoteImport.setImportStatus(EvernoteImport.IMPORT_SUCCESS);
                            baseMapper.updateById(evernoteImport);
                        }
                    } catch (Exception e) {
                        evernoteImport.setImportStatus(EvernoteImport.IMPORT_FAIL);
                        evernoteImport.setSuccessSize(0);
                        evernoteImport.setImportResult("导入失败,解析印象笔记过程中发生错误");
                        baseMapper.updateById(evernoteImport);
                        logger.error("导入印象笔记错误，笔记id:{},原因:{}", evernoteImport.getEvernoteId(), e);
                    }


                } else {
                    evernoteImport.setImportStatus(EvernoteImport.IMPORT_FAIL);
                    evernoteImport.setSuccessSize(0);
                    evernoteImport.setImportResult("导入失败,没有找到印象笔记文件");
                    baseMapper.updateById(evernoteImport);
                }
            }
        }
    }


    @Override
    public List<EvernoteImport> queryImportRecord(Page pagination, Integer userId) {
        QueryWrapper<EvernoteImport> ew = new QueryWrapper<>();
        ew.eq("user_id", userId);
        ew.orderBy(true, false, "import_date");
        return baseMapper.selectPage(pagination, ew).getRecords();
    }
}
