package com.moese.file.web;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.moese.file.entity.Doc;
import com.moese.file.entity.UploadFile;
import com.moese.file.service.IDocService;
import com.moese.file.service.IUploadFileService;
import com.moese.file.utils.HashUtil;
import com.moese.file.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

/**
 * @author HanKeQi
 * @date 2024/8/14
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private IUploadFileService uploadFileService;

    @Autowired
    private IDocService docService;

    private static final String BASE_UPLOAD_PATH = "/data/document/upload";


    @GetMapping
    public void  saveData() throws  Exception{
        String dataFile = "/syb";
        File[] files = new File(dataFile).listFiles();
        if (files != null){
            List<File> stream = Arrays.stream(files).filter(f->!f.getName().startsWith(".")).toList();
            stream.forEach(file -> {
                String title = file.getName();
                String filePath = String.format("%s/%s", dataFile, title);
                File[] filePaths = new File(filePath).listFiles();
                if (filePaths == null){

                    return;
                }
                List<File> fileStreams = Arrays.stream(filePaths).toList();
                fileStreams.forEach(fileStream ->{
                    String uuid = IdWorker.get32UUID();
                    String originalFilename = fileStream.getName();

                    String suffix = StringUtils.getFileSuffix(originalFilename);
                    File dist = new File(BASE_UPLOAD_PATH, uuid + "." + suffix);
                    String contentType = null;
                    try {
                        byte[] bytes = readFileToBytes(fileStream);
                        FileCopyUtils.copy(bytes, dist);
                        contentType = Files.probeContentType(fileStream.toPath());
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    UploadFile uploadFile = new UploadFile();
                    uploadFile.setFileId(uuid);
                    uploadFile.setFileDesc(originalFilename);
                    uploadFile.setFileName(dist.getName());
                    uploadFile.setFilePath(dist.getAbsolutePath());
                    uploadFile.setOriginalFileName(originalFilename);
                    uploadFile.setFileType(contentType);
                    uploadFile.setFileSize(fileStream.length());
                    uploadFile.setSha256(HashUtil.sha256(dist));
                    boolean save = uploadFileService.save(uploadFile);
                    Doc dbDoc = docService.queryDocBySha256(uploadFile.getSha256(), 1);
                    if (dbDoc != null) {
                        uploadFileService.deleteUploadFile(uploadFile.getFileId());
                        return;
                    }
                    docService.saveDoc(1, uploadFile);

                    System.out.println(uploadFile.toString());
                    System.out.println(fileStream.getName());
                });

            });
        }



    }


    public static byte[] readFileToBytes(File file) throws IOException{
        FileInputStream in = org.apache.commons.io.FileUtils.openInputStream(file);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] bytes = new byte[2048000];
        int len = 0;
        while((len=in.read(bytes)) != -1){
            bout.write(bytes,0,len);
        }
        return bout.toByteArray();
    }


//    public static void main(String[] args) throws Exception {
//        TestController testController = new TestController();
////        testController.saveData();
//    }

}
