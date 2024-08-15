package com.moese.file.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * @since 2018-07-04
 */
@Data
@ToString
@TableName("tb_evernote_import")
public class EvernoteImport implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int IMPORT_WAIT = 0;
    public static final int IMPORT_ING = 1;
    public static final int IMPORT_SUCCESS = 2;
    public static final int IMPORT_FAIL = 3;


    /**
     * id
     */
    @TableId(value = "evernote_id", type = IdType.AUTO)
    private Integer evernoteId;
    /**
     * 导入日期
     */
    @TableField("import_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date importDate;
    /**
     * 成功条数
     */
    @TableField("success_size")
    private Integer successSize;
    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;
    /**
     * 导入状态,0表示等待导入，1表示导入中，2表示导入成功，3表示导入失败
     */
    @TableField("import_status")
    private Integer importStatus;
    /**
     * 印象笔记文件id
     */
    @TableField("evernote_file_id")
    private String evernoteFileId;
    /**
     * 导入结果，中文描述
     */
    @TableField("import_result")
    private String importResult;


}
