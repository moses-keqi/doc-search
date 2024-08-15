package com.moese.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.moese.file.entity.EvernoteImport;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zxc
 * @since 2018-07-04
 */
public interface IEvernoteImportService extends IService<EvernoteImport> {

    @Transactional
    boolean importEverNote(MultipartFile file, Integer userId);

    @Transactional
    void importEverNoteSync(Integer importId);

    List<EvernoteImport> queryImportRecord(Page pagination, Integer userId);
}
