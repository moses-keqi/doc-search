package com.moese.file.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * <p>
 *
 * </p>
 *
 * @author zxc
 * @since 2018-06-22
 */
@Data
@ToString
@TableName("tb_doc")
@Document(indexName = "doc")
@Setting(
    replicas = 0, refreshInterval = "5s"
)
@Mapping(mappingPath = "/es/doc_mappings.json")
public class Doc implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @TableField(exist = false)
    private String id;
    @TableId(value = "doc_id", type = IdType.AUTO)
    private Integer docId;
    @TableField("doc_name")
    private String docName;
    @TableField("doc_size")
    private Long docSize;
    @TableField("doc_sha256")
    private String docSha256;
    @TableField("doc_create_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date docCreateDate;
    @TableField("doc_user_id")
    private Integer docUserId;
    @TableField("doc_file_id")
    private String docFileId;
    @TableField("doc_open")
    private Integer docOpen;
    @TableField("doc_type")
    private String docType;
    @TableField("doc_title")
    private String docTitle;
    @TableField("doc_content")
    private String docContent;
    @TableField("doc_delete")
    private Integer docDelete;

    //文档是否被索引了
    @TableField("doc_index")
    private Integer docIndex;

    @TableField(exist = false)
    private List<String> docContentHighLightList;

    @TableField(exist = false)
    private List<String> docTitleHighLightList;
    @TableField("doc_modify_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date docModifyDate;
    @TableField("source")
    private String source;
    @TableField("source_url")
    private String sourceUrl;
    //文档状态
    @TableField("doc_status")
    //0表示等待索引，1表示索引成功，2表示索引失败
    private Integer docStatus;

    @TableField("doc_convert")
    //0表示等待转换，1表示转换成功，2表示转换失败
    private Integer docConvert;

}
