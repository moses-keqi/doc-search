package com.moese.file.evernote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;

/**
 *
 * Title: EverNoteHelper.java
 *
 * @author zxc
 * @time 2018/7/3 下午4:25
 */
public class EverNoteHelper {

    private static Logger logger = LoggerFactory.getLogger(EverNoteHelper.class);

    public static boolean parseEverNoteFile(File enexFile,EverNoteCallBack everNoteCallBack){
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            EverNoteSaxHandler handler = new EverNoteSaxHandler(everNoteCallBack);
            parser.parse(new FileInputStream(enexFile), handler);
            return true;
        } catch (Exception e) {
            logger.error("parse everNote error",e);
        }
        return false;
    }

}
