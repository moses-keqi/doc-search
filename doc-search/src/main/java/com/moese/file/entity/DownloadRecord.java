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
@TableName("tb_download_record")
public class DownloadRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 下载id
     */
    @TableId(value = "download_id", type = IdType.AUTO)
    private Integer downloadId;
    /**
     * 下载人
     */
    @TableField("download_user_id")
    private Integer downloadUserId;
    /**
     * 下载日期
     */
    @TableField("download_date")
    private Date downloadDate;
    /**
     * 下载的文档id
     */
    @TableField("doc_id")
    private Integer docId;

}
