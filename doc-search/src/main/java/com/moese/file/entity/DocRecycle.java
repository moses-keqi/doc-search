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
@TableName("tb_doc_recycle")
public class DocRecycle implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 回收id
     */
    @TableId(value = "recycle_id", type = IdType.AUTO)
    private Integer recycleId;
    /**
     * 文档id
     */
    @TableField("doc_id")
    private Integer docId;
    /**
     * 回收日期
     */
    @TableField("recycle_date")
    private Date recycleDate;

}
