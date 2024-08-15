package com.moese.file.utils;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

/**
 * Created by zxc on 2017/11/6.
 */
public class ClassPathUtils {

    public static String getClassPathFile(String filename) {
        try {
            return IOUtils.toString(getInputStream(filename), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream getInputStream(String fileName) {
        InputStream in = ClassPathUtils.class.getResourceAsStream(fileName);
        return in;
    }

    public static String getClassFilePath(String filename) {
        return ClassPathUtils.class.getResource(filename).getPath();
    }
}
