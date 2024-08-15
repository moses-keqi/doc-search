package com.moese.file.config;

import com.moese.file.service.IDocService;
import com.moese.file.service.IThreadPoolExecutorService;
import com.moese.file.entity.Doc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 *
 * Title: YtdsStartConfig.java
 *
 * @author zxc
 * @time 2018/6/28 下午5:39
 */
@Component
public class YtdsStartConfig {

    @Autowired
    private IDocService docService;
    @Autowired
    private IThreadPoolExecutorService executorService;

    @PostConstruct
    public void init(){
        indexDocTask();
        convertDocTask();
        deleteDocTask();
        convertDocErrorTask();
    }

    private void deleteDocTask(){
        List<Doc> docList = docService.queryNeedDeleteDoc();
        for(Doc doc:docList){
            executorService.execute(()->{
                docService.deleteDocSync(doc);
            });
        }
    }

    private void indexDocTask(){
        List<Doc> docList = docService.queryNeedIndexDoc();
        for(Doc doc:docList){
            executorService.execute(()->{
                docService.indexDocSync(doc);
            });
        }
    }

    private void convertDocTask(){
        List<Doc> docList = docService.queryNeedConvertDoc();
        for(Doc doc:docList){
            executorService.singleExecute(()->{
                docService.convertDocSync(doc);
            });
        }
    }

    /**
     * index success but convert error
     */
    private void convertDocErrorTask(){
        List<Doc> docList = docService.queryNeedConvertDocError();
        for(Doc doc:docList){
            executorService.singleExecute(()->{
                docService.convertDocDataSync(doc);
            });
        }
    }



}
