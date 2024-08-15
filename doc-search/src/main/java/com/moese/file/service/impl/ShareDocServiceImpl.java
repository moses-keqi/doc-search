package com.moese.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moese.file.service.IShareDocService;
import com.moese.file.entity.ShareDoc;
import com.moese.file.mapper.ShareDocMapper;
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
public class ShareDocServiceImpl extends ServiceImpl<ShareDocMapper, ShareDoc> implements
        IShareDocService {

}
