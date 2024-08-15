package com.moese.file.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

/**
 * Title: OfficeUtils.java
 *
 * @author zxc
 * @time 2018/6/29 下午5:20
 */
@Slf4j
public class OfficeUtils {

    public static ConvertResult convertPdf(String sofficePath, File srcFile, File targetDir) {
        ///Applications/LibreOffice.app/Contents/MacOS/soffice
        // --headless --invisible --convert-to
        // html --outdir pdf-html/ *.pdf
        List<String> commands = new ArrayList<>();
        String outputDir = targetDir.getAbsolutePath();
        commands.add(sofficePath);
        commands.add("--headless");
        commands.add("--invisible");
        commands.add("--convert-to");
        commands.add("pdf");
        commands.add("--outdir");
        commands.add(outputDir);
        commands.add(srcFile.getAbsolutePath());
        log.info("开始转换pdf,命令行是:{}", org.apache.commons.lang3.StringUtils.joinWith(" ", commands));
        try {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            String result = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            log.info("convertPdf from {} output {}:\n result:{}\n", srcFile.getAbsolutePath(),
                outputDir, result);
            process.destroy();
            File targetFile = new File(targetDir,
                StringUtils.getFileNameWithoutSuffix(srcFile.getName()) + ".pdf");
            log.info("目标文件是:{}", targetFile.getAbsolutePath());
            if (targetFile.exists()) {
                log.info("{} 文档转化成功", srcFile.getAbsolutePath());
                return new ConvertResult(targetFile.getAbsolutePath(), true);
            } else {
                log.info("{} 文档转化失败", srcFile.getAbsolutePath());
                return new ConvertResult(null, false);
            }
        } catch (IOException e) {
            log.error("convertPdf", e);
        }
        return new ConvertResult(null, false);
    }


}
