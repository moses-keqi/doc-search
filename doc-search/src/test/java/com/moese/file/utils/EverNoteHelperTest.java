package com.moese.file.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moese.file.evernote.EverNoteHelper;

import java.io.File;
import org.junit.Test;

/**
 * Title: EverNoteUtilsTest.java
 *
 * @author zxc
 * @time 2018/7/3 下午2:07
 */
public class EverNoteHelperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void test() throws InterruptedException {
        EverNoteHelper.parseEverNoteFile(new File("/Users/zxc/Desktop/文档/我的笔记.enex"), everNote -> {
            try {
                System.out.println(objectMapper.writeValueAsString(everNote));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test2() throws InterruptedException {
        EverNoteHelper.parseEverNoteFile(new File("/Users/zxc/Desktop/文档/部分笔记.enex"), everNote -> {
            try {
                System.out.println(objectMapper.writeValueAsString(everNote));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }
}
