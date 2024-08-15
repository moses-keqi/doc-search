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
@TableName("tb_login_log")
public class LoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 登录id
     */
    @TableId(value = "login_id", type = IdType.AUTO)
    private Integer loginId;
    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;
    /**
     * 登录ip
     */
    @TableField("login_ip")
    private String loginIp;
    /**
     * 登录日期
     */
    @TableField("login_date")
    private Date loginDate;
    /**
     * 登录来源
     */
    @TableField("login_from")
    private String loginFrom;

}
