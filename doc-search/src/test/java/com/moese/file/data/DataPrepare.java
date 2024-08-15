package com.moese.file.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moese.file.entity.Doc;
import com.moese.file.utils.HashUtil;
import com.moese.file.utils.StringUtils;
import com.moese.file.utils.TikaUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * Title: DataPrepare.java
 *
 * @author zxc
 * @time 2018/6/23 下午6:01
 */
public class DataPrepare {

    public void scanDoc(String path, File saveFile) throws IOException {
        File dir = new File(path);
        File[] files = dir.listFiles(pathname -> {
            if (pathname.isFile()) {
                String name = pathname.getName().toLowerCase();
                if (name.endsWith(".doc")) {
                    return true;
                }
                if (name.endsWith(".pdf")) {
                    return true;
                }
                if (name.endsWith(".docx")) {
                    return true;
                }
                if (name.endsWith(".ppt")) {
                    return true;
                }
                if (name.endsWith(".pptx")) {
                    return true;
                }
                if (name.endsWith(".txt")) {
                    return true;
                }
            }
            return false;
        });
        int length = 40;
        if (files.length < 1024) {
            length = files.length;
        }
        List<Doc> docList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            String content = TikaUtils.parseContent(files[i]);
            Doc doc = new Doc();
            doc.setDocContent(content);
            doc.setDocId(i);
            doc.setDocCreateDate(new Date());
            doc.setDocDelete(0);
            doc.setDocModifyDate(new Date());
            doc.setDocName(files[i].getName());
            doc.setDocOpen(0);
            doc.setDocSha256(HashUtil.sha256(files[i]));
            doc.setDocSize(files[i].length());
            doc.setDocTitle(files[i].getName());
            doc.setDocType(StringUtils.getFileSuffix(files[i].getName()));
            doc.setDocUserId(1);
            docList.add(doc);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        FileUtils
            .writeStringToFile(saveFile, objectMapper.writeValueAsString(docList), "utf-8");
    }


    @Test
    public void prepare() throws IOException {
        scanDoc("/Users/zxc/文档", new File("testfile.json"));
    }
}
