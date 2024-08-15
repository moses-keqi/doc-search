package com.moese.file.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moese.file.annotation.Login;
import com.moese.file.entity.Doc;
import com.moese.file.entity.EvernoteImport;
import com.moese.file.entity.UploadFile;
import com.moese.file.entity.User;
import com.moese.file.exception.SystemException;
import com.moese.file.json.ActionMessage;
import com.moese.file.json.PageResult;
import com.moese.file.service.IDocService;
import com.moese.file.service.IEvernoteImportService;
import com.moese.file.service.IUploadFileService;
import com.moese.file.utils.StringUtils;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.POST;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Title: DocController.java
 *
 * @author zxc
 * @time 2018/6/24 下午8:47
 */
@Controller
@RequestMapping("/doc")
public class DocController {

    @Autowired
    private IDocService docService;
    @Autowired
    private IUploadFileService uploadFileService;
    @Autowired
    private IEvernoteImportService evernoteImportService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Login
    @RequestMapping(value = "/everNoteContent")
    @ResponseBody
    public ActionMessage everNoteContent(Integer docId) {
        ActionMessage am = ActionMessage.fail();
        try {
            if (docId != null) {
                Doc doc = docService.getBaseMapper().selectById(docId);
                if (doc != null) {
                    if ("evernote".equals(doc.getDocType())) {
                        String content = uploadFileService.readUploadFileString(doc.getDocFileId());
                        doc.setDocContent(content);
                        am.setCode(ActionMessage.success);
                        am.setData(doc);
                        am.setMessage("文档读取成功");
                        return am;
                    }
                }
            }
            am.setCode(ActionMessage.fail);
            am.setMessage("文档获取失败");
            return am;
        } catch (Exception e) {
            logger.error("everNoteContent", e);
        }
        return am;
    }


    @Login
    @RequestMapping(value = "/pdfFile")
    public void pdfFile(Integer docId, HttpServletResponse res, HttpServletRequest request) {
        if (docId != null) {
            Doc doc = docService.getBaseMapper().selectById(docId);
            if (doc != null) {
                File pdfFile = docService.getPdfFile(doc);
                if (pdfFile == null) {
                    if (doc.getDocType().equals("pdf")) {
                        UploadFile uploadFile = uploadFileService.getUploadFile(doc.getDocFileId());
                        if (uploadFile != null) {
                            pdfFile = new File(uploadFile.getFilePath());
                            if (!pdfFile.exists()) {
                                pdfFile = null;
                            }
                        }


                    }
                }
                if (pdfFile != null) {
                    try {
                        String etag = doc.getDocFileId();
                        if (etag.equals(request.getHeader("If-None-Match"))) {
                            res.setStatus(304);
                            return;
                        }
                        res.setContentType("application/pdf");
                        res.addHeader("Etag", etag);
                        res.setHeader("Content-Length", Long.toString(pdfFile.length()));
                        String pdfName =
                                StringUtils.getFileNameWithoutSuffix(doc.getDocName()) + ".pdf";
                        res.setHeader("Content-Disposition",
                                "attachment; filename=" + URLEncoder.encode(pdfName, "utf-8"));
                        FileUtils.copyFile(pdfFile, res.getOutputStream());
                        res.getOutputStream().flush();
                    } catch (IOException e) {
                        res.setStatus(404);
                        logger.error("获取上传文件出现io异常", e);
                    }
                    return;
                }
            }
        }
        res.setStatus(404);
    }

    @Login
    @RequestMapping("/queryDocTypeNumber")
    @ResponseBody
    public ActionMessage queryDocTypeNumber(HttpSession httpSession) {
        ActionMessage am = ActionMessage.fail();
        try {
            User user = (User) httpSession.getAttribute("user");
            Map map = docService.queryDocTypeNumber(user.getUserId());
            am.setMessage("统计数据查询成功");
            am.setCode(ActionMessage.success);
            am.setData(map);
            return am;
        } catch (Exception e) {
            am.setMessage("文档类型统计数据查询失败");
        }
        return am;
    }

    @Login
    @RequestMapping(value = "/downloadDocFile")
    public void downloadDocFile(Integer docId, HttpServletResponse res,
                                HttpServletRequest request) {
        if (docId != null) {
            Doc doc = docService.getBaseMapper().selectById(docId);
            if (doc != null) {
                UploadFile uploadFile = uploadFileService.getUploadFile(doc.getDocFileId());
                if (uploadFile != null) {
                    try {
                        String etag = doc.getDocFileId();
                        if (etag.equals(request.getHeader("If-None-Match"))) {
                            res.setStatus(304);
                            return;
                        }
                        File docFile = new File(uploadFile.getFilePath());
                        res.setContentType("application/octet-stream");
                        res.addHeader("Etag", etag);
                        res.setHeader("Content-Length", Long.toString(docFile.length()));
                        //String pdfName = StringUtils.getFileNameWithoutSuffix(doc.getDocName())+".pdf";
                        res.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder
                                .encode(uploadFile.getOriginalFileName(), "utf-8"));
                        //res.setHeader("Content-Type","application/octet-stream;charset=utf-8");
                        FileUtils.copyFile(docFile, res.getOutputStream());
                        res.getOutputStream().flush();
                    } catch (IOException e) {
                        res.setStatus(404);
                        logger.error("获取上传文件出现io异常", e);
                    }
                    return;
                }
            }
        }
        res.setStatus(404);
    }

    @RequestMapping("/deleteDoc")
    @ResponseBody
    @Login
    public ActionMessage deleteDoc(String docIdArray, HttpSession httpSession) {
        ActionMessage am = ActionMessage.fail();
        try {
            User user = (User) httpSession.getAttribute("user");
            ObjectMapper objectMapper = new ObjectMapper();
            List<Integer> docIdList = objectMapper
                    .readValue(docIdArray, new TypeReference<List<Integer>>() {
                    });
            for (Integer docId : docIdList) {
                Doc doc = docService.getBaseMapper().selectById(docId);
                if (doc == null) {
                    am.setMessage("文档不存在");
                    return am;
                } else {
                    if (!doc.getDocUserId().equals(user.getUserId())) {
                        am.setMessage("不可以删除不是你的文档");
                        return am;
                    } else {
                        docService.deleteDoc(docId);
                    }
                }
            }
            am.setCode(ActionMessage.success);
            am.setMessage("文档已删除");
            return am;
        } catch (Exception e) {
            logger.error("deleteDoc", e);
            am.setMessage("删除失败");
        }
        return am;
    }

    @RequestMapping("/findDoc")
    @ResponseBody
    @Login
    public ActionMessage findDoc(String paramsJson, Integer current, Integer size,
                                 HttpSession httpSession) {
        ActionMessage am = ActionMessage.fail();
        try {
            User user = (User) httpSession.getAttribute("user");
            Page pagination = new Page<>(current, size);
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, Object> params = objectMapper.readValue(paramsJson, Map.class);
            params.put("docUserId", user.getUserId());
            List<Doc> list = docService.findDoc(pagination, params);
            PageResult<Doc> pageResult = new PageResult<>();
            pageResult.setList(list);
            pageResult.setPage(pagination);
            am.setCode(ActionMessage.success);
            am.setMessage("文档查找成功");
            am.setData(pageResult);
            return am;
        } catch (Exception e) {
            logger.error("findDoc", e);
            am.setMessage("文档查找失败");
        }
        return am;

    }


    @RequestMapping("/searchDoc")
    @ResponseBody
    @Login
    public ActionMessage searchDoc(String paramsJson, Integer page, Integer size,
                                   HttpSession httpSession) {
        ActionMessage am = ActionMessage.fail();
        try {
            User user = (User) httpSession.getAttribute("user");
            ObjectMapper objectMapper = new ObjectMapper();
            Map params = objectMapper.readValue(paramsJson, Map.class);
            params.put("userId", user.getUserId());
            List<Doc> docList = docService.searchDoc(params, page - 1, size);
            PageResult<Doc> pageResult = new PageResult<>();
            pageResult.setList(docList);
            pageResult.setPage(new Page(page, size));
            am.setCode(ActionMessage.success);
            am.setData(pageResult);
            am.setMessage("搜索成功");
            return am;
        } catch (Exception e) {
            am.setMessage("搜索失败");
            logger.error("searchDoc", e);
        }
        return am;
    }

    @RequestMapping("/isDocExist")
    @ResponseBody
    @Login
    public ActionMessage isDocExist(String sha256, HttpSession httpSession) {
        ActionMessage am = ActionMessage.fail();
        try {
            User user = (User) httpSession.getAttribute("user");
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(sha256)) {
                Doc dbDoc = docService.queryDocBySha256(sha256, user.getUserId());
                if (dbDoc != null) {
                    am.setMessage("该文档已存在");
                    am.setCode(ActionMessage.success);
                    am.setData(true);
                    return am;
                } else {
                    am.setMessage("该文档不存在");
                    am.setCode(ActionMessage.success);
                    am.setData(false);
                    return am;
                }

            } else {
                am.setMessage("sha256不能为空");
                return am;
            }
        } catch (Exception e) {
            logger.error("isDocExist", e);
            am.setMessage("查询文档是否存在失败");
        }
        return am;
    }


    @RequestMapping("/uploadDoc")
    @ResponseBody
    @Login
    public ActionMessage uploadDoc(@RequestParam(value = "file") MultipartFile file,
                                   HttpSession httpSession) {
        ActionMessage am = ActionMessage.fail();
        try {
            User user = (User) httpSession.getAttribute("user");
            if (file == null) {
                am.setMessage("上传的文档不能为空");
                return am;
            }
            UploadFile uploadFile = uploadFileService.saveFile(file, file.getOriginalFilename());
            Doc dbDoc = docService.queryDocBySha256(uploadFile.getSha256(), user.getUserId());
            if (dbDoc != null) {
                uploadFileService.deleteUploadFile(uploadFile.getFileId());
                am.setMessage("该文件已存在无需重复上传");
                return am;
            }
            docService.saveDoc(user.getUserId(), uploadFile);
            am.setMessage("文档上传成功");
            am.setCode(ActionMessage.success);
            return am;
        } catch (Exception e) {
            logger.error("uploadDoc", e);
            am.setMessage("文档上传失败");
        }
        return am;
    }

    /**
     * 导入印象笔记
     */
    @RequestMapping("/importEverNote")
    @ResponseBody
    @Login
    public ActionMessage importEverNote(@RequestParam(value = "file") MultipartFile file,
                                        HttpSession httpSession) {
        ActionMessage am = ActionMessage.fail();
        try {
            User user = (User) httpSession.getAttribute("user");
            boolean success = evernoteImportService.importEverNote(file, user.getUserId());
            if (success) {
                am.setMessage("印象笔记上传成功，后台正在导入");
                am.setCode(ActionMessage.success);
                am.setData(true);
                return am;
            } else {
                am.setMessage("印象笔记导入失败");
            }
        } catch (SystemException e) {
            am.setMessage(e.getMessage());
        } catch (Exception e) {
            am.setMessage("印象笔记导入失败");
            logger.error("importEverNote", e);
        }
        return am;
    }


    @RequestMapping("/queryEverNoteImportRecord")
    @ResponseBody
    @Login
    public ActionMessage queryEverNoteImportRecord(Integer current, Integer size,
                                                   HttpSession httpSession) {
        ActionMessage am = ActionMessage.fail();
        try {
            Page pagination = new Page<>(current, size);
            User user = (User) httpSession.getAttribute("user");
            List<EvernoteImport> evernoteImportList = evernoteImportService
                    .queryImportRecord(pagination, user.getUserId());
            PageResult<EvernoteImport> pageResult = new PageResult<>();
            pageResult.setPage(pagination);
            pageResult.setList(evernoteImportList);
            am.setData(pageResult);
            am.setCode(ActionMessage.success);
            am.setMessage("成功查询印象笔记导入记录");
        } catch (SystemException e) {
            am.setMessage(e.getMessage());
        } catch (Exception e) {
            am.setMessage("印象笔记导入记录查询失败");
            logger.error("queryEverNoteImportRecord", e);
        }
        return am;
    }
    @PostMapping("/convertDoc")
    @ResponseBody
    @Login
    public ActionMessage convertDoc(Integer docId){
        ActionMessage am = ActionMessage.fail();
        try {
            Doc doc = docService.getById(docId);
            if (doc != null){
                docService.convertDocDataSync(doc);
                am.setMessage("成功");
                am.setCode(ActionMessage.success);
                return am;
            }
        }catch (Exception e){
            am.setMessage("转换异常");
        }
        return am;
    }


}
