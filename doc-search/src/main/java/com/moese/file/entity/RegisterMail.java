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
@TableName("tb_register_mail")
public class RegisterMail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 注册id
     */
    @TableId(value = "register_id", type = IdType.AUTO)
    private Integer registerId;
    /**
     * 注册邮箱
     */
    private String email;
    /**
     * 注册代码
     */
    private String code;
    /**
     * 发送时间
     */
    @TableField("send_date")
    private Date sendDate;

}
