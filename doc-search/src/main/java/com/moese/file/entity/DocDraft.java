package com.moese.file.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

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
@TableName("tb_doc_draft")
public class DocDraft implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 草稿文档id
     */
    @TableId("draft_doc_id")
    private Integer draftDocId;
    /**
     * 草稿保存时间
     */
    @TableField("draft_save_date")
    private Date draftSaveDate;
    /**
     * 草稿文档内容
     */
    @TableField("draft_doc_content")
    private String draftDocContent;
    /**
     * 草稿文档标题
     */
    @TableField("draft_doc_title")
    private String draftDocTitle;


}
