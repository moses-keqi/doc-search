package com.moese.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moese.file.entity.UploadFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zxc
 * @since 2018-05-09
 */
public interface IUploadFileService extends IService<UploadFile> {

    UploadFile saveFile(MultipartFile file, String desc);

    UploadFile saveFile(String content, String suffix, String name, String desc);

    UploadFile getUploadFileBySha256(String sha256);

    UploadFile getUploadFile(String fileId);

    String readUploadFileString(String fileId);

    boolean deleteUploadFile(String fileId);

    boolean exist(String fileId);
}
