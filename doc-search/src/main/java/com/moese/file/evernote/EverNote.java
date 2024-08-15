package com.moese.file.evernote;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Title: EverNote.java
 *
 * @author zxc
 * @time 2018/7/3 下午4:24
 */
@Data
@ToString
public class EverNote {

    private String title;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date created;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date updated;
    private String author;
    private String source;
    private String sourceUrl;
    private String sourceApplication;

}
