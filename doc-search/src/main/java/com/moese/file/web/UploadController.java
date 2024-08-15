package com.moese.file.web;

import com.moese.file.entity.UploadFile;
import com.moese.file.json.ActionMessage;
import com.moese.file.service.IUploadFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by zxc on 2017/9/7.
 */
@Controller
@RequestMapping("/upload")
@Api(value = "/upload", description = "后台管理使用,文件上传统一接口")
public class UploadController extends BaseController {

    @Autowired
    private IUploadFileService uploadFileService;
    private Logger logger = LoggerFactory.getLogger(UploadController.class);

    @ApiOperation(value = "上传文件", notes = "上传文件", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "form", name = "file", value = "文件", required = true, dataType = "file")
    })
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public ActionMessage<UploadFile> upload(@RequestParam(value = "file") MultipartFile file) {
        ActionMessage<UploadFile> am = ActionMessage.fail();
        try {
            UploadFile uploadFile = uploadFileService.saveFile(file, null);
            if (uploadFile != null) {
                uploadFile.setFilePath(null);
                am.setData(uploadFile);
                am.setCode(ActionMessage.success);
                am.setMessage("文件上传成功");
                return am;
            } else {
                am.setMessage("文件上传失败");
            }
            return am;
        } catch (Exception e) {
            logger.error("上传失败", e);
            am.setExceptionMessage(e);
        }
        return am;
    }

    @ApiOperation(value = "上传多个文件", notes = "上传多个文件", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "form", name = "files", value = "多个文件，由于swagger不支持multiple,所以无法展示效果", required = true, dataType = "file")
    })
    @RequestMapping("/uploadFiles")
    @ResponseBody
    public ActionMessage<List<UploadFile>> uploadFiles(
        @RequestParam(value = "files") MultipartFile files[]) {
        ActionMessage<List<UploadFile>> am = ActionMessage.fail();
        try {
            List<UploadFile> fileList = new ArrayList<>();
            for (MultipartFile file : files) {
                UploadFile uploadFile = uploadFileService.saveFile(file, null);
                if (uploadFile == null) {
                    am.setMessage("文件上传失败");
                    return am;
                } else {
                    uploadFile.setFilePath(null);
                    fileList.add(uploadFile);
                }
            }
            am.setData(fileList);
            am.setCode(ActionMessage.success);
            am.setMessage("文件上传成功");
            return am;
        } catch (Exception e) {
            logger.error("上传失败", e);
            am.setExceptionMessage(e);
        }
        return am;
    }

    @ApiOperation(value = "读取文件", notes = "传递fileId，如果是图片可直接img src='/file/fileId'使用", httpMethod = "GET", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", name = "fileId", value = "文件的id", required = true, dataType = "string")
    })
    @RequestMapping(value = "/file/{fileId}", method = RequestMethod.GET)
    public void getFile(@PathVariable String fileId, HttpServletResponse res,
        HttpServletRequest request) {

        if (!StringUtils.isEmpty(fileId)) {
            UploadFile uploadFile = uploadFileService.getUploadFile(fileId);
            if (uploadFile == null) {
                res.setStatus(404);
            } else {

                try {
                    File file = new File(uploadFile.getFilePath());
                    String etag = file.lastModified() + fileId;
                    String lastModify = new Date(file.lastModified()).toString();
                    if (etag.equals(request.getHeader("If-None-Match"))) {
                        res.setStatus(304);
                        return;
                    }
                    if (lastModify.equals(request.getHeader("If-Modified-Since"))) {
                        res.setStatus(304);
                        return;
                    }
                    if (file.exists()) {
                        res.setContentType(uploadFile.getFileType());
                        res.addHeader("Etag", etag);
                        res.addHeader("Last-Modified", lastModify);
                        res.setHeader("Content-Length", uploadFile.getFileSize().toString());
                        res.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder
                            .encode(uploadFile.getOriginalFileName(), "utf-8"));
                        res.setHeader("Content-Type", "application/octet-stream;charset=utf-8");
                        FileUtils.copyFile(file, res.getOutputStream());
                        res.getOutputStream().flush();
                    } else {
                        res.setStatus(404);
                    }
                } catch (IOException e) {
                    res.setStatus(404);
                    logger.error("获取上传文件出现io异常", e);
                }
            }
        } else {
            res.setStatus(404);
        }
    }

    @ApiOperation(value = "删除文件", notes = "传递fileId，删除文件", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "form", name = "fileId", value = "文件的id", required = true, dataType = "string")
    })
    @RequestMapping(value = "/deleteFile", method = RequestMethod.POST)
    @ResponseBody
    public ActionMessage<Boolean> deleteFile(String fileId) {
        ActionMessage<Boolean> am = ActionMessage.fail();
        try {
            boolean delete = uploadFileService.deleteUploadFile(fileId);
            if (delete) {
                am.setCode(ActionMessage.success);
                am.setData(delete);
                am.setMessage("删除成功");
            } else {
                am.setCode(ActionMessage.fail);
                am.setData(delete);
                am.setMessage("删除失败");
            }

        } catch (Exception e) {
            logger.error("upload/deleteFile", e);
            am.setExceptionMessage(e);
        }
        return am;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ActionMessage ExceptionHandler(Exception exception) {
        ActionMessage am = ActionMessage.fail();
        if (exception != null && (exception.getCause() instanceof MaxUploadSizeExceededException)) {
            am.setMessage("上传的文件超过2MB的大小,无法上传");
            return am;
        } else {
            am.setMessage("服务器出现未知异常");
            logger.error("未知异常", exception);
        }
        return null;
    }


}
