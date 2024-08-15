package com.moese.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moese.file.entity.RegisterMail;
import com.moese.file.entity.User;
import com.moese.file.exception.SystemException;
import com.moese.file.mapper.UserMapper;
import com.moese.file.service.IUserService;
import com.moese.file.utils.HashUtil;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zxc
 * @since 2018-06-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public User login(String name, String pwd) {
        QueryWrapper<User> ew = new QueryWrapper<>();
        ew.eq("user_name", name);
        User user = this.baseMapper.selectOne(ew);
        if (user == null) {
            return null;
        } else {
            String hashResult = HashUtil.sha256(user.getUserSalt() + pwd);
            if (user.getUserPwd().equals(hashResult)) {
                user.setUserToken(IdWorker.get32UUID());
                this.updateById(user);
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean existUserByName(String name) {
        QueryWrapper<User> ew = new QueryWrapper<>();
        ew.eq("user_name", name);
        User user = this.baseMapper.selectOne(ew);
        if (user == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean existUserByEmail(String email) {
        QueryWrapper<User> ew = new QueryWrapper<>();
        ew.eq("user_mail", email);
        User user = this.baseMapper.selectOne(ew);
        if (user == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void sendRegisterEmail(String email) {
//        String code = new RandomString(System.currentTimeMillis()).randomNumeric(6);
//        RegisterMail registerMail = new RegisterMail();
//        registerMail.setCode(code);
//        registerMail.setEmail(email);
//        registerMail.setSendDate(new Date());
//        registerMailMapper.insert(registerMail);
//        mailService.sendMail("云图文档搜索,欢迎注册用户,这是你的注册码",
//            String.format("您的注册验证码为%s。10分钟内有效。如非您本人操作，可忽略本消息。", code), email);
    }

    @Override
    public User registerUser(User user, String mailCode) {
        if (existUserByName(user.getUserName())) {
            throw new SystemException("用户名已存在");
        }
        if (existUserByEmail(user.getUserMail())) {
            throw new SystemException("该邮箱已存在");
        }
        if (StringUtils.isEmpty(mailCode)) {
            throw new SystemException("邮箱验证码不存在");
        }
        if (StringUtils.isEmpty(user.getNickName())) {
            throw new SystemException("昵称不能为空");
        }
        if (StringUtils.isEmpty(user.getUserPwd())) {
            throw new SystemException("密码不能为空");
        }
        Date now = new Date();

        QueryWrapper<RegisterMail> ew = new QueryWrapper<>();
        ew.eq("code", mailCode);
        ew.eq("email", user.getUserMail());
        ew.gt("send_date", DateUtils.addMinutes(now, -10));
        //TODO
//        List<RegisterMail> registerMailList = registerMailMapper.selectList(ew);
//        long count = registerMailList.stream().filter(registerMail -> {
//            if (registerMail.getCode().equals(mailCode)) {
//                return true;
//            } else {
//                return false;
//            }
//        }).count();
//        if (count > 0) {
//            user.setUserToken(null);
//            user.setRegisterDate(new Date());
//            user.setUserSalt(IdWorker.get32UUID());
//            user.setUserPwd(HashUtil.sha256(user.getUserSalt() + user.getUserPwd()));
//            this.insert(user);
//        }
        return null;
//        return user;
    }


    @Override
    public User findUserByToken(String token) {
        QueryWrapper<User> ew = new QueryWrapper<>();
        ew.eq("user_token", token);
        User user = this.baseMapper.selectOne(ew);
        return user;
    }

}
