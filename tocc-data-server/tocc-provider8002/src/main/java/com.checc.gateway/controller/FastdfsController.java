package com.checc.gateway.controller;

import com.checc.common.utils.StringUtils;
import com.checc.hdfs.FastDFSWrapper;
import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RestController
@Slf4j
public class FastdfsController {

    @Autowired
    private FastDFSWrapper fastDFSWrapper;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @GetMapping(value = "/file/upload")
    public void upload() throws FileNotFoundException {
        File file = new File("d://text.jpg");
        String fileName = file.getName();
        String exName = fileName.substring(fileName.lastIndexOf(".")+1);
        FileInputStream inputStream = new FileInputStream(file);

        StorePath storePath = fastFileStorageClient.uploadFile(inputStream,file.length(),exName,null);
        log.info(storePath.getPath());
        log.info(storePath.getFullPath());
    }

    @DeleteMapping(value = "/file/delete")
    public void delete(@RequestParam("urlPath") String urlPath)  {
        try {
            String group = urlPath.substring(0,urlPath.indexOf("/"));
            String path = urlPath.substring(urlPath.indexOf("/")+1);
            log.warn("group:"+group+"<-->path:"+path);
            FileInfo fileInfo = fastFileStorageClient.queryFileInfo(group,path);
            if (StringUtils.isNotNull(fileInfo)) {
                fastDFSWrapper.delFile(urlPath);
            }
        } catch (Exception e) {
            log.error("文件路径不存在");
        }
    }

}
