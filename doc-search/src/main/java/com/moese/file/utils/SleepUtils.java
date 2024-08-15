package com.moese.file.utils;

/**
 *
 * Title: SleepUtils.java
 *
 * @author zxc
 * @time 2018/6/29 下午10:08
 */
public class SleepUtils {

    public static synchronized void  sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
