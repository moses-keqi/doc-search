package com.moese.file.utils;

import com.moese.file.config.BaseConfig;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Title: OfficeUtilsTest.java
 *
 * @author zxc
 * @time 2018/6/29 下午5:22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OfficeUtilsTest {

    @Autowired
    private BaseConfig baseConfig;

    @Test
    public void convertPdfTest() {
        String docFile = ClassPathUtils.getClassFilePath("/doc");
        File targetFileDir = new File(docFile);
        File[] fileList = targetFileDir.listFiles(pathname -> !pathname.getName().endsWith("pdf"));

        if (fileList != null) {
            for (File file : fileList) {
                ConvertResult result = OfficeUtils
                    .convertPdf(baseConfig.getSofficePath(), file,
                        targetFileDir);
                Assert.assertTrue(result.isSuccess());
            }
        }

    }
}
