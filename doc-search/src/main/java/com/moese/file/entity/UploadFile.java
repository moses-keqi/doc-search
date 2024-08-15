package com.moese.file.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author zxc
 * @since 2018-05-09
 */
@Data
@ToString
@TableName("tb_upload_file")
@ApiModel("上传文件实体")
public class UploadFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("file_id")
    @ApiModelProperty("文件id")
    private String fileId;
    @ApiModelProperty(hidden = true)
    @TableField("file_path")
    private String filePath;
    @ApiModelProperty(hidden = true)
    private String sha256;
    @TableField("file_size")
    @ApiModelProperty("文件大小")
    private Long fileSize;
    @TableField("file_type")
    @ApiModelProperty("文件类型")
    private String fileType;
    @TableField("file_desc")
    @ApiModelProperty(hidden = true)
    private String fileDesc;
    @TableField("file_name")
    @ApiModelProperty("文件名字")
    private String fileName;
    @ApiModelProperty("文件原生名字")
    @TableField("original_file_name")
    private String originalFileName;
}
