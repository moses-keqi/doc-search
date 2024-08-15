package com.moese.file.service;


/**
 * Title: IMailService.java
 *
 * @author zxc
 * @time 2018/6/24 下午1:00
 */
public interface IMailService {
    //void sendMail(Email email);

    void sendMail(String title, String content, String to);
}
