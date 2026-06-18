package com.petboarding.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.petboarding.common.config.OssConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
public class OssUtils {

    @Autowired
    private OssConfig ossConfig;

    /**
     * 上传文件到OSS
     * @param file 文件
     * @param dir 目录（如 pet/、record/）
     * @return 文件URL
     */
    public String upload(MultipartFile file, String dir) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String objectName = dir + UUID.randomUUID().toString() + extension;

        OSS ossClient = null;
        try (InputStream inputStream = file.getInputStream()) {
            ossClient = new OSSClientBuilder().build(ossConfig.getEndpoint(),
                    ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossConfig.getBucketName(), objectName, inputStream);
            ossClient.putObject(putObjectRequest);

            return ossConfig.getBaseUrl() + objectName;
        } catch (IOException e) {
            log.error("OSS上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败");
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 上传文件到OSS（本地模拟版本 — 直接返回base64或使用本地存储）
     * 在实际部署中替换为真实的OSS上传
     */
    public String uploadLocal(MultipartFile file, String dir) {
        // 简化版：返回模拟URL（实际环境请使用上方upload方法）
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "/upload/" + dir + UUID.randomUUID().toString() + extension;
    }

    /**
     * 删除OSS文件
     */
    public void delete(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(ossConfig.getBaseUrl())) {
            return;
        }
        String objectName = fileUrl.substring(ossConfig.getBaseUrl().length());

        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(ossConfig.getEndpoint(),
                    ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
            ossClient.deleteObject(ossConfig.getBucketName(), objectName);
        } catch (Exception e) {
            log.error("OSS删除失败: {}", e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
