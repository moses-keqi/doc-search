package com.moese.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

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
@TableName("tb_search_record")
public class SearchRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索id
     */
    @TableId(value = "search_record_id", type = IdType.AUTO)
    private Integer searchRecordId;
    /**
     * 搜索关键词
     */
    @TableField("search_keyword")
    private String searchKeyword;
    /**
     * 搜索后阅读了哪篇文档
     */
    @TableField("doc_id")
    private Integer docId;
    /**
     * 搜索人
     */
    @TableField("user_id")
    private Integer userId;

}
