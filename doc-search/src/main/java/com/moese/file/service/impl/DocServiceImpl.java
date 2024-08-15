package com.moese.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moese.file.config.BaseConfig;
import com.moese.file.entity.Doc;
import com.moese.file.entity.UploadFile;
import com.moese.file.mapper.DocMapper;
import com.moese.file.repository.DocRepository;
import com.moese.file.service.IDocService;
import com.moese.file.service.IThreadPoolExecutorService;
import com.moese.file.service.IUploadFileService;
import com.moese.file.utils.ConvertResult;
import com.moese.file.utils.OfficeUtils;
import com.moese.file.utils.StringUtils;
import com.moese.file.utils.TikaUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zxc
 * @since 2018-06-22
 */
@Service
public class DocServiceImpl extends ServiceImpl<DocMapper, Doc> implements IDocService {

    @Autowired
    private DocRepository docRepository;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;
    @Autowired
    private IUploadFileService uploadFileService;
    @Autowired
    private IThreadPoolExecutorService executorService;
    @Autowired
    private BaseConfig baseConfig;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //private final DefaultEntityMapper defaultEntityMapper;

    public DocServiceImpl() {
        //defaultEntityMapper = new DefaultEntityMapper();
    }


    @Override
    public List<Doc> queryNeedIndexDoc() {
        QueryWrapper<Doc> ew = new QueryWrapper<>();
        ew.eq("doc_status", 0);
        return baseMapper.selectList(ew);
    }

    @Override
    public List<Doc> queryNeedDeleteDoc() {
        QueryWrapper<Doc> ew = new QueryWrapper<>();
        ew.eq("doc_delete", 1);
        return baseMapper.selectList(ew);
    }

    @Override
    public List<Doc> queryNeedConvertDoc() {
        QueryWrapper<Doc> ew = new QueryWrapper<>();
        ew.eq("doc_convert", 0);
        ew.eq("source", "file");
        ew.ne("doc_type", "pdf");
        return baseMapper.selectList(ew);
    }

    @Override
    public List<Doc> queryNeedConvertDocError() {
        QueryWrapper<Doc> ew = new QueryWrapper<>();
        ew.eq("doc_convert", 2);
        ew.eq("source", "file");
        ew.ne("doc_type", "pdf");
        return baseMapper.selectList(ew);
    }


    @Override
    public void convertDocSync(Doc doc) {
        if (doc.getDocConvert() != 1) {
            return;
        }
        convertDocDataSync(doc);
    }


    @Override
    public void indexDocSync(Doc doc) {

        logger.info("index Task docId:{},docName:{}", doc.getDocId(), doc.getDocName());
        if (doc == null) {
            logger.error("doc is null");
        } else {
            UploadFile uploadFile = uploadFileService.getUploadFile(doc.getDocFileId());
            if (uploadFile == null) {
                logger.error("upload file not exists,docId:{}", doc.getDocId());
            } else {
                File file = new File(uploadFile.getFilePath());
                if (file.exists()) {
                    String content = TikaUtils.parseContent(file);
                    if (content == null) {
                        doc.setDocStatus(2);
                        this.updateById(doc);
                        logger.error("tika解析文件内容出错,fileId:{}", file.getAbsolutePath());
                    } else {
                        doc.setDocIndex(1);
                        doc.setDocStatus(1);
                        doc.setDocContent(content);
                        doc.setId(doc.getDocId().toString());
                        //save doc to elastic
                        this.saveDocToEs(doc);
                        //文档内容不存到mysql
                        doc.setDocContent(null);
                        //update doc to mysql
                        this.updateById(doc);
                    }
                } else {
                    logger.error("file not exists,docId:{}", doc.getDocId());
                }
            }
        }

    }

    @Transactional
    @Override
    public void deleteDocSync(Doc doc) {
        if (doc.getDocDelete() == 1) {

            docRepository.deleteById(doc.getDocId().toString());
            this.baseMapper.deleteById(doc);
            logger.info("删除转换的pdf文件");
            File pdfFile = getPdfFile(doc);
            if (pdfFile != null && pdfFile.exists()) {
                boolean deleted = pdfFile.delete();
                if (deleted) {
                    logger.info("删除转换的pdf文档成功:{}", pdfFile.getAbsolutePath());
                } else {
                    logger.error("删除转换的pdf文档失败:{}", pdfFile.getAbsolutePath());
                }
            }
            String fileId = doc.getDocFileId();
            if (fileId != null) {
                uploadFileService.deleteUploadFile(fileId);
            }
        }
    }

    @Override
    @Transactional
    public void saveDoc(Integer userId, UploadFile uploadFile) {
        Doc doc = new Doc();
        doc.setDocTitle(uploadFile.getOriginalFileName());
        doc.setDocUserId(userId);
        doc.setDocType(StringUtils.getFileSuffix(uploadFile.getOriginalFileName()));
        doc.setDocSize(uploadFile.getFileSize());
        doc.setDocOpen(0);
        doc.setDocName(uploadFile.getOriginalFileName());
        doc.setDocModifyDate(new Date());
        doc.setDocDelete(0);
        doc.setDocCreateDate(new Date());
        doc.setDocFileId(uploadFile.getFileId());
        doc.setDocIndex(0);
        doc.setDocStatus(0);
        doc.setSource("file");
        doc.setDocSha256(uploadFile.getSha256());
        if (doc.getDocType().equals("pdf")) {
            doc.setDocConvert(1);
        } else {
            doc.setDocConvert(0);
        }
        this.baseMapper.insert(doc);
        executorService.execute(() -> indexDocSync(doc));
        executorService.singleExecute(() -> convertDocSync(doc));

    }


    private void saveDocToEs(Doc doc) {
        docRepository.save(doc);
    }

    @Override
    public Doc queryDocBySha256(String sha256, Integer userId) {
        QueryWrapper<Doc> ew = new QueryWrapper<>();
        ew.eq("doc_sha256", sha256);
        ew.eq("doc_user_id", userId);
        List<Doc> docList = baseMapper.selectList(ew);
        if (docList.size() == 0) {
            return null;
        } else {
            return docList.get(0);
        }
    }


    @Override
    public File getPdfFile(Doc doc) {
        if (doc.getDocFileId() != null) {
            UploadFile uploadFile = uploadFileService.getUploadFile(doc.getDocFileId());
            if (doc.getDocConvert() == 1) {
                String path = uploadFile.getFilePath();
                File srcFile = new File(path);
                File dir = new File(srcFile.getParentFile(), "pdf");
                File pdfFile = new File(dir, uploadFile.getFileId() + ".pdf");
                if (pdfFile.exists()) {
                    return pdfFile;
                }
            }
        }
        return null;
    }


    @Override
    public List<Doc> findDoc(Page pagination,
                             Map<String, Object> params) {
        String docName = (String) params.get("docName");
        String docType = (String) params.get("docType");
        Integer docUserId = (Integer) params.get("docUserId");
        return baseMapper.findDoc(pagination, docName, docType, docUserId);
    }

    @Transactional
    @Override
    public void deleteDoc(Integer docId) {
        Doc doc = new Doc();
        doc.setId(docId.toString());
        doc.setDocId(docId);
        doc.setDocDelete(1);
        baseMapper.updateById(doc);

        //update到es
//        IndexRequest indexRequest = new IndexRequest();
//        indexRequest.source("docDelete", 1);
        Document document = Document.create();
        document.put("docDelete", 1);
        UpdateQuery updateQuery = UpdateQuery.builder(doc.getId()).withDocument(document).build();
//        UpdateQuery updateQuery = new UpdateQueryBuilder().withId(doc.getId()).withClass(Doc.class)
//            .withIndexRequest(indexRequest).build();
        elasticsearchTemplate.update(updateQuery, IndexCoordinates.of("doc"));
        Doc doc2 = baseMapper.selectById(docId);
        executorService.execute(() -> {
            deleteDocSync(doc2);
        });
    }


    @Override
    public Map queryDocTypeNumber(Integer userId) {
        return baseMapper.queryDocTypeNumber(userId);
    }

    private void completeDoc(List<Doc> docList) {
        List<Integer> docIdList = docList.stream().map(Doc::getDocId).collect(Collectors.toList());
        QueryWrapper<Doc> ew = new QueryWrapper<>();
        ew.in("doc_id", docIdList);
        Map<Integer, Doc> map = new HashMap<>();
        List<Doc> docs = baseMapper.selectList(ew);
        if (CollectionUtils.isNotEmpty(docs)){
            docs.forEach(doc -> {
                map.put(doc.getDocId(), doc);
            });
            docList.forEach(doc -> doc.setDocConvert(map.get(doc.getDocId()).getDocConvert()));
        }

    }


    /**
     * es搜索文档
     */
    @Override
    public List<Doc> searchDoc(Map<String, Object> params, int page, int size) {

        Long dateStart = (Long) params.get("dateStart");
        Long dateEnd = (Long) params.get("dateEnd");
        String docType = (String) params.get("docType");
        Integer userId = (Integer) params.get("userId");
        //文档大小
        //0表示不限，1表示0Kb-128kb,2表示128kb-512kb,
        //3表示512kb-1mb,4表示1mb-5mb,5表示5mb以上
        Integer docSizeLevel = (Integer) params.get("docSizeLevel");
        String words = (String) params.get("words");
        //搜索词位置 0表示所有位置，1表示在文档内容，2表示在文档标题
        Integer wordsLocation = (Integer) params.get("wordsLocation");
        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();

        List<QueryBuilder> filterCnd = filterQuery.must();
        if (dateStart != null && dateEnd != null) {
            filterCnd.add(QueryBuilders.rangeQuery("docModifyDate").gte(dateStart).lt(dateEnd));
        }
        if (docType != null && !docType.equals("all")) {
            BoolQueryBuilder docTypeQuery = QueryBuilders.boolQuery();
            filterCnd.add(docTypeQuery);
            List<QueryBuilder> docTypeCnd = docTypeQuery.should();
            if (docType.equals("pdf")) {
                docTypeCnd.add(QueryBuilders.termQuery("docType", "pdf"));
            }
            if (docType.equals("powerpoint")) {
                docTypeCnd.add(QueryBuilders.termQuery("docType", "ppt"));
                docTypeCnd.add(QueryBuilders.termQuery("docType", "pptx"));
            }
            if (docType.equals("word")) {
                docTypeCnd.add(QueryBuilders.termQuery("docType", "doc"));
                docTypeCnd.add(QueryBuilders.termQuery("docType", "docx"));
            }
            if (docType.equals("excel")) {
                docTypeCnd.add(QueryBuilders.termQuery("docType", "xls"));
                docTypeCnd.add(QueryBuilders.termQuery("docType", "xlsx"));
            }
            if (docType.equals("html")) {
                docTypeCnd.add(QueryBuilders.termQuery("docType", "html"));
            }
        }

        if (docSizeLevel != null) {
            Long sizeStart = null;
            Long sizeEnd = null;
            switch (docSizeLevel) {
                case 1:
                    sizeStart = 0L;
                    sizeEnd = 1024 * 128L;
                    break;
                case 2:
                    sizeStart = 1024 * 128L;
                    sizeEnd = 1024 * 512L;
                    break;
                case 3:
                    sizeStart = 1024 * 512L;
                    sizeEnd = 1024 * 1024L;
                    break;
                case 4:
                    sizeStart = 1024 * 1024L;
                    sizeEnd = 1024 * 1024 * 5L;
                    break;
                case 5:
                    sizeStart = 1024 * 1024 * 5L;
                    break;
            }
            RangeQueryBuilder docSizeRange = null;
            if (sizeStart != null) {
                docSizeRange = QueryBuilders.rangeQuery("docSize");
                docSizeRange.from(sizeStart);
            }
            if (sizeEnd != null) {
                docSizeRange.to(sizeEnd);
            }
            if (docSizeRange != null) {
                filterCnd.add(docSizeRange);
            }
        }
        if (userId != null) {
            filterCnd.add(QueryBuilders.termQuery("docUserId", userId));
        }
        filterCnd.add(QueryBuilders.termQuery("docDelete", 0));
        if (wordsLocation != null) {
            MultiMatchQueryBuilder search = null;
            if (wordsLocation == 0) {
                search = QueryBuilders.multiMatchQuery(words, "docTitle", "docContent");
            } else if (wordsLocation == 1) {
                search = QueryBuilders.multiMatchQuery(words, "docContent");
            } else if (wordsLocation == 2) {
                search = QueryBuilders.multiMatchQuery(words, "docTitle");
            }
            if (search != null) {
                query.must(search);
            }
        } else {
            query.must(QueryBuilders.multiMatchQuery(words, "docTitle", "docContent"));
        }

        query.filter(filterQuery);
        searchQuery.withQuery(query);
        searchQuery.withHighlightFields(
            new HighlightBuilder.Field("docContent").fragmentSize(400),
//.highlighterType("fvh").fragmentSize(200).numOfFragments(10),
            new HighlightBuilder.Field("docTitle")//.highlighterType("fvh")
        );
        NativeSearchQuery nativeSearchQuery = searchQuery.build();
        logger.info("query cnd:{}", nativeSearchQuery.getQuery());
        nativeSearchQuery.addSourceFilter(new FetchSourceFilter(
            new String[]{"docSize", "docTitle", "docType", "docCreateDate", "docId"}, null));
        Pageable pageable = PageRequest.of(page, size);
        nativeSearchQuery.setPageable(pageable);
        ElasticsearchOperations elasticsearchOperations;
        SearchHits<Doc> search = elasticsearchTemplate.search(nativeSearchQuery, Doc.class);
        List<Doc> docList = new ArrayList<>();
        SearchHitSupport
            .searchPageFor(search, pageable).getContent().forEach(doc -> {
            docList.add(searchHitToDoc(doc));
        });
        completeDoc(docList);
        return docList;
    }

    @Override
    public void convertDocDataSync(Doc doc) {
        if (doc.getDocConvert() == 1){
            return;
        }
        logger.info("开始转换文档:{}", doc.getDocName());
        UploadFile uploadFile = uploadFileService.getUploadFile(doc.getDocFileId());
        if (uploadFile == null) {
            logger.error("upload file not exists,docId:{}", doc.getDocId());
        } else {
            File file = new File(uploadFile.getFilePath());
            if (file.exists()) {
                File targetDir = new File(file.getParentFile(), "pdf");
                ConvertResult result = OfficeUtils
                        .convertPdf(baseConfig.getSofficePath(), file, targetDir);
                if (!result.isSuccess()) {
                    doc.setDocConvert(2);
                    this.updateById(doc);
                    logger.error("文档转换失败,{}", doc.getDocName());
                } else {
                    doc.setDocConvert(1);
                    logger.info("文档转换成功,{}", doc.getDocName());
                    this.updateById(doc);
                }
            } else {
                logger.error("file not exists,docId:{}", doc.getDocId());
            }
        }
    }

    private Doc searchHitToDoc(
        org.springframework.data.elasticsearch.core.SearchHit<Doc> searchHit) {
        Doc doc = searchHit.getContent();
        List<String> docContentHighlightField = searchHit.getHighlightFields().get("docContent");
        List<String> docTitleHighlightField = searchHit.getHighlightFields().get("docTitle");
        if (docContentHighlightField != null) {
            StringBuilder sb = new StringBuilder();
            docContentHighlightField.forEach(sb::append);
            doc.setDocContent(sb.toString());
        }
        if (docTitleHighlightField != null) {
            StringBuilder sb = new StringBuilder();
            docTitleHighlightField.forEach(sb::append);
            doc.setDocTitle(sb.toString());
        }
        return doc;
    }

}
