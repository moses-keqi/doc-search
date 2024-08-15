package com.moese.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moese.file.service.ISearchRecordService;
import com.moese.file.entity.SearchRecord;
import com.moese.file.mapper.SearchRecordMapper;
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
public class SearchRecordServiceImpl extends
    ServiceImpl<SearchRecordMapper, SearchRecord> implements ISearchRecordService {

}
