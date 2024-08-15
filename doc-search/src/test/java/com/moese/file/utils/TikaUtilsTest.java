package com.moese.file.utils;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;

/**
 * Title: TikaUtilsTest.java
 *
 * @author zxc
 * @time 2018/5/10 22:39
 */


public class TikaUtilsTest {

    @Test
    public void parseContentTest() {
        String docFile = ClassPathUtils.getClassFilePath("/doc");
        File targetFileDir = new File(docFile);
        File[] fileList = targetFileDir.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                String content = TikaUtils.parseContent(file);
                Assert.assertNotNull(content);
                Assert.assertFalse(content.isEmpty());
            }
        }
    }

}
