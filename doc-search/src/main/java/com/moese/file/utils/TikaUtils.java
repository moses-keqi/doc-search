package com.moese.file.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.util.StringUtil;
import org.apache.commons.io.FileUtils;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.txt.UniversalEncodingDetector;
import org.apache.tika.sax.BodyContentHandler;

/**
 * Title: TikaUtils.java
 *
 * @author zxc
 * @time 2018/4/7 19:15
 */

@Slf4j
public class TikaUtils {


    public static String parseContent(File f) {
        String content = null;
        try {
            InputStream stream = FileUtils.openInputStream(f);
            content = parseContent(stream);
            if (StringUtil.isEmpty(content)) {
                content = parseTxt(f);
            }
            if (content != null) {
                return content;
            }
        } catch (Exception e) {
            log.error("tika parse error", e);
        }
        return content;
    }

    private static String parseTxt(File file) throws IOException {
        InputStream stream1 = FileUtils.openInputStream(file);
        EncodingDetector detector = new UniversalEncodingDetector();
        Charset charset = detector.detect(new BufferedInputStream(stream1), new Metadata());
        stream1.close();
        if (charset != null) {
            return FileUtils.readFileToString(file, charset);
        } else {
            return null;
        }
    }

    public static String parseContent(InputStream stream) {
        String content = null;
        try {
            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);
            Metadata metadata = new Metadata();
            parser.parse(stream, handler, metadata);
            content = handler.toString();
        } catch (Exception e) {
            log.error("tika parse error", e);
        }
        return content;
    }

}
