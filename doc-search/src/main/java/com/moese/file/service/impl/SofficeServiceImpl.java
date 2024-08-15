package com.moese.file.service.impl;

import com.moese.file.config.BaseConfig;
import com.moese.file.service.ISofficeService;
import com.moese.file.utils.OfficeUtils;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Title: SofficeServiceImpl.java
 *
 * @author zxc
 * @time 2018/6/29 下午5:03
 */
@Service
public class SofficeServiceImpl implements ISofficeService {

    @Autowired
    private BaseConfig baseConfig;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean convertPdf(File srcFile, File targetFile) {

        return OfficeUtils.convertPdf(baseConfig.getSofficePath(), srcFile, targetFile).isSuccess();
    }
}
