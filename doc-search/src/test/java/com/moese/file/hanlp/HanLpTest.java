package com.moese.file.hanlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import com.moese.file.evernote.EverNoteHelper;
import com.moese.file.utils.TikaUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import org.junit.Test;

/**
 * Title: HanLpTest.java
 *
 * @author zxc
 * @time 2018/7/4 上午8:13
 */
public class HanLpTest {

    public static int i = 1;

    @Test
    public void testExtractKeyword() {
        EverNoteHelper.parseEverNoteFile(new File("/Users/zxc/Desktop/文档/我的笔记.enex"), everNote -> {
            String content = everNote.getContent();
            content = TikaUtils.parseContent(new ByteArrayInputStream(content.getBytes()));
            List<String> keywordList = HanLP.extractKeyword(content, 10);
            System.out.println("title:" + everNote.getTitle());
            System.out.println(content);
            System.out.println(keywordList);

        });
    }

    @Test
    public void testStopWord() {
        String text = "小区居民有的反对喂养流浪猫，<span style='color:red' style=\"\"></span>而有的居民却赞成喂养这些小宝贝";
        // 可以动态修改停用词词典
        CoreStopWordDictionary.add("居民");
        CoreStopWordDictionary.add("'");
        CoreStopWordDictionary.add("<");
        CoreStopWordDictionary.add(">");
        CoreStopWordDictionary.add("><");
        CoreStopWordDictionary.add("/");
        System.out.println(NotionalTokenizer.segment(text));
    }
}
