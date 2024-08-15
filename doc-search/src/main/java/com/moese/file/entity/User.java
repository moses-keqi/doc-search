package com.moese.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@TableName("tb_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;
    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;
    /**
     * sha256来哈希用户密码（not use md5）sha256(salt+pwd)
     */
    @TableField("user_pwd")
    @JsonIgnore
    private String userPwd;
    /**
     * 用户邮箱
     */
    @TableField("user_mail")
    @JsonIgnore
    private String userMail;
    /**
     * 用户注册日期
     */
    @TableField("register_date")
    @JsonIgnore
    private Date registerDate;
    /**
     * 用户token
     */
    @TableField("user_token")
    private String userToken;
    /**
     * 加盐
     */
    @TableField("user_salt")
    @JsonIgnore
    private String userSalt;
    /**
     * 用户昵称
     */
    @TableField("nick_name")
    private String nickName;
    /**
     * 用户头像文件id
     */
    @TableField("portrait_id")
    private String portraitId;

}
