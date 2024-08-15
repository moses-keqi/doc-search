package com.moese.file.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moese.file.entity.Doc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Title: DocServiceTest.java
 *
 * @author zxc
 * @time 2018/6/23 下午7:18
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DocServiceTest {

    @Autowired
    private IDocService docService;
    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void testQuery2() {
        Map<String, Object> params = new HashMap<>();
        params.put("dateStart", DateUtils.addDays(new Date(), -2));
        params.put("dateEnd", new Date());
        params.put("docSizeLevel", 4);
        params.put("docType", "pptx");
        params.put("words", "test");
        List<Doc> result = docService.searchDoc(params, 1, 10);
        if (result != null) {
            result.forEach(doc -> {
                try {
                    System.out.println(objectMapper.writeValueAsString(doc));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }


    }

    @Test
    public void testQuery3() {
        Map<String, Object> params = new HashMap<>();
        params.put("dateStart", DateUtils.addDays(new Date(), -2));
        params.put("dateEnd", new Date());
        params.put("words", "test");
        List<Doc> result = docService.searchDoc(params, 0, 10);
        if (result != null) {
            result.forEach(doc -> {
                try {
                    System.out.println(objectMapper.writeValueAsString(doc));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }


    }
}
