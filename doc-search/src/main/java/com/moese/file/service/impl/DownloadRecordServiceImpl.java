package com.moese.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moese.file.entity.DownloadRecord;
import com.moese.file.mapper.DownloadRecordMapper;
import com.moese.file.service.IDownloadRecordService;
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
public class DownloadRecordServiceImpl extends
    ServiceImpl<DownloadRecordMapper, DownloadRecord> implements IDownloadRecordService {

}
