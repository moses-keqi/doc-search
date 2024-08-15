package com.moese.file.service;

/**
 * Title: IEsSqlService.java
 * Copyright: Copyright (C) 2002 - 2018 GuangDong Eshore Technology Co. Ltd
 * Company: 广东亿迅科技有限公司 IT互联网部
 *
 * @author zxc
 * @time 2018/4/20 18:03
 */


public interface IEsSqlService {
    String sql(String sql);

    String explainSql(String sql);
}
