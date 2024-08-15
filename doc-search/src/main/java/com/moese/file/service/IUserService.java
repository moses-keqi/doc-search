package com.moese.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moese.file.entity.User;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zxc
 * @since 2018-06-22
 */
public interface IUserService extends IService<User> {

    User login(String name, String pwd);

    boolean existUserByName(String name);

    boolean existUserByEmail(String email);

    void sendRegisterEmail(String email);

    User registerUser(User user, String mailCode);

    User findUserByToken(String token);
}
