package com.moese.file.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moese.file.entity.User;
import com.moese.file.exception.SystemException;
import com.moese.file.json.ActionMessage;
import com.moese.file.service.IUserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Title: UserController.java
 *
 * @author zxc
 * @time 2018/6/24 上午11:21
 */
@Controller
@RequestMapping("/usr")
public class UserController {

    @Autowired
    private IUserService userService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/login")
    @ResponseBody
    public ActionMessage login(String name, String pwd, HttpServletResponse response,
                               HttpSession httpSession) {
        ActionMessage am = ActionMessage.fail();
        try {
            User user = userService.login(name, pwd);
            if (user == null) {
                am.setMessage("登录失败");
            } else {
                Cookie cookie = new Cookie("token", user.getUserToken());
                //cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                //一个月
                cookie.setMaxAge(60 * 60 * 24 * 30);
                response.addCookie(cookie);
                am.setData(user);
                httpSession.setAttribute("user", user);
                am.setCode(ActionMessage.success);
                //am.setData();
                am.setMessage("登录成功");
            }
        } catch (SystemException e) {
            am.setMessage(e.getMessage());
        } catch (Exception e) {
            am.setMessage("登录失败");
            logger.error("login", e);
        }
        return am;
    }

    @RequestMapping("/existUserByName")
    @ResponseBody
    public ActionMessage existUserByName(String name) {
        ActionMessage am = ActionMessage.fail();
        try {
            boolean existUserByName = userService.existUserByName(name);
            am.setData(existUserByName);
            am.setCode(ActionMessage.success);
            return am;
        } catch (SystemException e) {
            am.setMessage(e.getMessage());
        } catch (Exception e) {
            am.setMessage("用户名查询失败");
            logger.error("existUserByName", e);
        }
        return am;
    }

    @RequestMapping("/existUserByEmail")
    @ResponseBody
    public ActionMessage existUserByEmail(String email) {
        ActionMessage am = ActionMessage.fail();
        try {
            boolean existUserByEmail = userService.existUserByEmail(email);
            am.setData(existUserByEmail);
            am.setCode(ActionMessage.success);
            return am;
        } catch (SystemException e) {
            am.setMessage(e.getMessage());
        } catch (Exception e) {
            am.setMessage("邮箱查询失败");
            logger.error("existUserByEmail", e);
        }
        return am;
    }


    @RequestMapping("/sendRegisterEmail")
    @ResponseBody
    public ActionMessage sendRegisterMail(String email) {
        ActionMessage am = ActionMessage.fail();
        try {
            userService.sendRegisterEmail(email);
            am.setData(true);
            am.setCode(ActionMessage.success);
            return am;
        } catch (SystemException e) {
            am.setMessage(e.getMessage());
        } catch (Exception e) {
            am.setMessage("注册邮件发送失败");
            logger.error("sendRegisterEmail", e);
        }
        return am;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ActionMessage register(String userJson, String mailCode) {
        ActionMessage am = ActionMessage.fail();
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            User user = objectMapper.readValue(userJson, User.class);
            user = userService.registerUser(user, mailCode);
            am.setData(user);
            am.setCode(ActionMessage.success);
            return am;
        } catch (SystemException e) {
            am.setMessage(e.getMessage());
        } catch (Exception e) {
            am.setMessage("用户注册失败");
            logger.error("register", e);
        }
        return am;
    }
}
