package com.moese.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moese.file.entity.LoginLog;
import com.moese.file.mapper.LoginLogMapper;
import com.moese.file.service.ILoginLogService;
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
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements
    ILoginLogService {

}
