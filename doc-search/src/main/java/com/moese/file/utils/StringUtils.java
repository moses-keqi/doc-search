package com.moese.file.utils;

import java.io.UnsupportedEncodingException;

/**
 * Title: StringUtils.java
 * @author zxc
 * @time 2018/4/24 17:39
 */


public class StringUtils {
    public static String byteToStringUTF8(byte[]bytes){
        try {
            return new String (bytes,"utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] stringToByteUTF8(String s){
        try {
            return s.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFileSuffix(String fileName){
        int pos=fileName.lastIndexOf(".");
        if(pos>=0){
            if(pos+1!=fileName.length()){
                return fileName.substring(pos+1,fileName.length()).toLowerCase();
            }
        }
        return "";
    }

    public static String getFileNameWithoutSuffix(String fileName){
        int pos=fileName.lastIndexOf(".");
        if(pos>=0){
            if(pos+1!=fileName.length()){
                return fileName.substring(0,pos);
            }
        }
        return "";
    }
}
