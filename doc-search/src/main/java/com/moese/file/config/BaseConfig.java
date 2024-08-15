package com.moese.file.config;

import com.moese.file.utils.DateUtils;
import java.io.File;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Title: BaseConfig.java
 *
 * @author zxc
 * @time 2018/5/10 下午4:54
 */
@Configuration
@ConfigurationProperties(prefix = "base")
@EnableConfigurationProperties(BaseConfig.class)
@Data
public class BaseConfig {

    private String uploadDir;
    private String sofficePath;

    @PostConstruct
    public void init() {
        File file = new File(uploadDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    public File getTodayUploadDir() {
        String today = DateUtils.formatDate1(new Date());
        File t = new File(uploadDir, today);
        if (!t.exists()) {
            t.mkdirs();
        }
        return t;
    }
}
