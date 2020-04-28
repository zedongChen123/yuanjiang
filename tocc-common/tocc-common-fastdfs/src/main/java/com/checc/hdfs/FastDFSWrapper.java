package com.checc.hdfs;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.fdfs.ThumbImageConfig;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * FastDFS包装类
 */
@Component
@Slf4j
public class FastDFSWrapper {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Autowired
    private ThumbImageConfig thumbImageConfig;

    /**
     * 文件上传
     * 最后返回fastDFS中的文件名称;group1/M00/01/04/CgMKrVvS0geAQ0pzAACAAJxmBeM793.doc
     *
     * @param bytes     文件字节
     * @param fileSize  文件大小
     * @param extension 文件扩展名
     * @return fastDfs路径
     */
    public String uploadFile(byte[] bytes, long fileSize, String extension) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        StorePath storePath = fastFileStorageClient.uploadFile(byteArrayInputStream, fileSize, extension, null);
        log.info(storePath.getGroup() + "==" + storePath.getPath() + "======" + storePath.getFullPath());
        return storePath.getFullPath();
    }

    /**
     * 图片上传 原图+150x150缩略图
     * @param bytes     文件字节
     * @param fileSize  文件大小
     * @param extension 文件扩展名
     * @return fastDfs路径
     */
    public String[] uploadThumbFile(byte[] bytes, long fileSize, String extension) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        StorePath storePath = fastFileStorageClient.uploadImageAndCrtThumbImage(byteArrayInputStream, fileSize, extension, null);
        log.info(storePath.getGroup() + "==" + thumbImageConfig.getThumbImagePath(storePath.getPath()) + "======" + storePath.getFullPath());
        return new String[]{thumbImageConfig.getThumbImagePath(storePath.getFullPath()), storePath.getFullPath()};
    }

    /**
     * 下载文件
     *  返回文件字节流大小
     * @param fileUrl 文件URL
     * @return 文件字节
     * @throws IOException .
     */
    public byte[] downloadFile(String fileUrl) throws IOException {
        fileUrl = fileUrl.substring(fileUrl.indexOf("group"));
        String group = fileUrl.substring(0, fileUrl.indexOf("/"));
        String path = fileUrl.substring(fileUrl.indexOf("/") + 1);
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        log.info("group:"+group+"--path:"+path);
        return fastFileStorageClient.downloadFile(group, path, downloadByteArray);
    }

    /**
     * 删除图片
     * @param fileUrl 文件地址
     */
    public void delFile(String fileUrl) {
        fastFileStorageClient.deleteFile(fileUrl);
    }

}
