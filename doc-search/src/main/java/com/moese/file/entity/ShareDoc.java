package com.moese.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("tb_share_doc")
public class ShareDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分享id
     */
    @TableId(value = "share_id", type = IdType.AUTO)
    private Integer shareId;
    /**
     * 分享给哪个用户
     */
    @TableField("share_to")
    private Integer shareTo;
    /**
     * 分享日期
     */
    @TableField("share_date")
    private Date shareDate;
    /**
     * 文档id
     */
    @TableField("doc_id")
    private Integer docId;
    /**
     * 文档有效期，单位秒，0表示无限
     */
    @TableField("valid_time")
    private Integer validTime;

}
