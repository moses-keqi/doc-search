package com.moese.file.exception;

/**
 *
 * Title: SystemException.java
 *
 * @author zxc
 * @time 2018/6/24 下午1:29
 */
public class SystemException extends RuntimeException{

    private String message;

    public SystemException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
