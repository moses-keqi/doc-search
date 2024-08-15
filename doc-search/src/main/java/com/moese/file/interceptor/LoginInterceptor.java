package com.moese.file.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moese.file.annotation.Login;
import com.moese.file.entity.User;
import com.moese.file.json.ActionMessage;
import com.moese.file.service.IUserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Title: LoginInterceptor.java
 *
 * @author zxc
 * @time 2018/3/22 16:32
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {


    private IUserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginInterceptor(IUserService userService) {
        this.userService = userService;
    }


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            Login login = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
            if (login != null) {
                log.info("login拦截,{}", httpServletRequest.getRequestURL().toString());
                HttpSession httpSession = httpServletRequest.getSession(true);
                User user = (User) httpSession.getAttribute("user");
                if (user == null) {
                    Cookie cookies[] = httpServletRequest.getCookies();
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("token")) {
                            user = userService.findUserByToken(cookie.getValue());
                            if (user != null) {
                                httpSession.setAttribute("user", user);
                                return true;
                            }
                        }
                    }
                    ActionMessage am = ActionMessage.fail();
                    am.setCode(ActionMessage.unLogin);
                    am.setMessage("未登录");
                    httpServletResponse.setContentType("application/json;charset=UTF-8");
                    httpServletResponse.getWriter().print(objectMapper.writeValueAsString(am));
                    return false;
                }
            }
        }
        return true;
    }


}
