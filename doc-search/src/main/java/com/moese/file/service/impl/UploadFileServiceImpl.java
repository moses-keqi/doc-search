package com.moese.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moese.file.config.BaseConfig;
import com.moese.file.entity.UploadFile;
import com.moese.file.mapper.UploadFileMapper;
import com.moese.file.service.IUploadFileService;
import com.moese.file.utils.HashUtil;
import com.moese.file.utils.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
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
 * @since 2018-05-09
 */
@Service
public class UploadFileServiceImpl extends ServiceImpl<UploadFileMapper, UploadFile> implements
    IUploadFileService {

    @Autowired
    private BaseConfig baseConfig;
    private Logger logger = LoggerFactory.getLogger(UploadFileServiceImpl.class);


    @Override
    public UploadFile saveFile(MultipartFile file, String desc) {
        try {
            File todayDir = baseConfig.getTodayUploadDir();
            String uid = IdWorker.get32UUID();
            String suffix = StringUtils.getFileSuffix(file.getOriginalFilename());
            File dist = new File(todayDir, uid + "." + suffix);
            file.transferTo(dist);
            UploadFile uploadFile = new UploadFile();
            uploadFile.setFileId(uid);
            uploadFile.setFileDesc(desc);
            uploadFile.setFileName(file.getName());
            uploadFile.setFilePath(dist.getAbsolutePath());
            uploadFile.setOriginalFileName(file.getOriginalFilename());
            uploadFile.setFileType(file.getContentType());
            uploadFile.setFileSize(file.getSize());
            uploadFile.setSha256(HashUtil.sha256(dist));
            baseMapper.insert(uploadFile);
            return uploadFile;
        } catch (IOException e) {
            logger.error("文件上传transferTo错误", e);
        }
        return null;
    }

    @Override
    public UploadFile saveFile(String content, String suffix, String name, String desc) {
        try {
            File todayDir = baseConfig.getTodayUploadDir();
            String uid = IdWorker.get32UUID();
            //String suffix=StringUtils.getFileSuffix(file.getOriginalFilename());
            File dist = new File(todayDir, uid + "." + suffix);
            FileUtils.writeStringToFile(dist, content, "utf-8");
            UploadFile uploadFile = new UploadFile();
            uploadFile.setFileId(uid);
            uploadFile.setFileDesc(desc);
            uploadFile.setFileName(name + "." + suffix);
            uploadFile.setFilePath(dist.getAbsolutePath());
            uploadFile.setOriginalFileName(uploadFile.getFileName());
            uploadFile.setFileType(suffix);
            uploadFile.setFileSize(dist.length());
            uploadFile.setSha256(HashUtil.sha256(dist));
            baseMapper.insert(uploadFile);
            return uploadFile;
        } catch (IOException e) {
            logger.error("文件上传transferTo错误", e);
        }
        return null;
    }


    @Override
    public UploadFile getUploadFileBySha256(String sha256) {
        QueryWrapper<UploadFile> ew = new QueryWrapper<>();
        ew.eq("sha256", sha256);
        List<UploadFile> list = baseMapper.selectList(ew);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }


    @Override
    public UploadFile getUploadFile(String fileId) {
        return baseMapper.selectById(fileId);
    }

    @Override
    public String readUploadFileString(String fileId) {
        UploadFile uploadFile = getUploadFile(fileId);
        File file = new File(uploadFile.getFilePath());
        if (file.exists()) {
            try {
                return FileUtils.readFileToString(file, "utf-8");
            } catch (IOException e) {
                logger.error("readUploadFileString", e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean deleteUploadFile(String fileId) {
        UploadFile uploadFile = getUploadFile(fileId);
        if (uploadFile != null) {
            String filePath = uploadFile.getFilePath();
            File f = new File(filePath);
            if (f.exists()) {
                f.delete();
            }
            baseMapper.deleteById(uploadFile.getFileId());
            return true;
        } else {
            return false;
        }
    }


    private String getNameByPath(String path) {
        File f = new File(path);
        return f.getName();
    }

    @Override
    public boolean exist(String fileId) {
        if (fileId == null) {
            return false;
        }
        QueryWrapper<UploadFile> ew = new QueryWrapper<>();
        ew.eq("file_id", fileId);
        return this.count(ew) > 0;
    }


}
