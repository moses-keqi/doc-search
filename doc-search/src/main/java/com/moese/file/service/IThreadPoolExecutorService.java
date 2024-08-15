package com.moese.file.service;

/**
 * Title: IThreadPoolExecutorService.java
 *
 * @author zxc
 * @time 2018/3/29 17:22
 */


public interface IThreadPoolExecutorService {
    void singleExecute(Runnable runnable);

    void execute(Runnable runnable);
}
