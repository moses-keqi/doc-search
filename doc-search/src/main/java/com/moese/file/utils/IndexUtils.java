package com.moese.file.utils;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;

import java.util.List;

/**
 *
 * Title: IndexUtils.java
 *
 * @author zxc
 * @time 2018/6/4 下午4:30
 */
public class IndexUtils {
    public static enum WEIGHT{
        A,B,C,D
    }
    public static String toTsVector(String s,WEIGHT weight){
        s=filterString(s);
        List<Term> termList = IndexTokenizer.segment(s);
        StringBuffer sb=new StringBuffer();
        for (Term term : termList)
        {
            if(term.word.trim().equals("")){
                continue;
            }
            if(weight!=null){
                sb.append(term.word+":"+(term.offset+1)+weight.name());
            }else {
                sb.append(term.word+":"+(term.offset+1));
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    public static String toTsQuery(String s){
        s=filterString(s);
        List<Term> termList = IndexTokenizer.segment(s);
        StringBuffer sb=new StringBuffer();
        for (Term term : termList)
        {
            if(term.word.trim().equals("")){
                continue;
            }
            sb.append(term.word);
            sb.append("|");
        }
        String result=sb.toString();
        return result.substring(0,result.length()-1);
    }

//    public static String simpleIndex(String s){
//        List<Term> termList = IndexTokenizer.segment(s);
//
//    }

    public static String toTsVector(String s){
        return  toTsVector(s,null);
    }

    public static String filterString(String s){
        String result = s.replaceAll("[^(a-zA-Z0-9\\u4e00-\\u9fa5)]", " ");
        return result;
    }



}
