package com.moese.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.moese.file.entity.Doc;
import com.moese.file.entity.UploadFile;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zxc
 * @since 2018-06-22
 */
public interface IDocService extends IService<Doc> {


    List<Doc> queryNeedIndexDoc();

    List<Doc> queryNeedDeleteDoc();

    List<Doc> queryNeedConvertDoc();

    List<Doc> queryNeedConvertDocError();

    void convertDocSync(Doc doc);

    void indexDocSync(Doc doc);

    @Transactional
    void deleteDocSync(Doc doc);

    void saveDoc(Integer userId, UploadFile uploadFile);

    Doc queryDocBySha256(String sha256, Integer userId);

    File getPdfFile(Doc doc);

    List<Doc> findDoc(Page pagination, Map<String, Object> params);

    void deleteDoc(Integer docId);


    Map queryDocTypeNumber(Integer userId);

    List<Doc> searchDoc(Map<String, Object> params, int page, int size);

    void convertDocDataSync(Doc doc);
}
