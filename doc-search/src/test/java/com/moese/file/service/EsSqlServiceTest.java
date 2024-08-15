package com.moese.file.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * Title: EsSqlServiceTest.java
 *
 * @author zxc
 * @time 2018/6/23 下午2:38
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsSqlServiceTest {
    @Autowired
    private IEsSqlService esSqlService;

    @Test
    public void test(){
        String sql ="select /*! HIGHLIGHT(field1,pre_tags : ['<b>'], post_tags : ['</b>']  ) */ \n" +
                "/*! HIGHLIGHT(field2,pre_tags : ['<b>'], post_tags : ['</b>']  ) */ * from myIndex";
        String result = esSqlService.explainSql(sql);
        System.out.println(result);
    }

    @Test
    public void test2(){
        String sql ="select * from myindex where a='a' and (xx='xx' or oo='oo')";
        String result = esSqlService.explainSql(sql);
        System.out.println(result);
        //Sear
    }
}
