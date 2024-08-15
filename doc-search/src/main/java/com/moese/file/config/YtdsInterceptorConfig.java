package com.moese.file.config;

import com.moese.file.service.IUserService;
import com.moese.file.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Title: YtdsInterceptorConfig.java
 *
 * @author zxc
 * @time 2018/6/24 下午7:17
 */
@Configuration
@Slf4j
public class YtdsInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private IUserService userService;

    /**
     * 增加登录拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("addInterceptors:{}", "LoginInterceptor");
        InterceptorRegistration loginInterceptor = registry
            .addInterceptor(new LoginInterceptor(userService));
        loginInterceptor.addPathPatterns("/**");
    }


}
