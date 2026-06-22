package com.petboarding.common.controller;

import com.petboarding.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@Tag(name = "文件上传", description = "图片/文件上传（本地存储）")
public class FileUploadController {

    @Value("${upload.local-path:${user.dir}/uploads}")
    private String uploadPath;

    @Value("${upload.base-url:http://localhost:8080/uploads}")
    private String baseUrl;

    private Path resolvedUploadPath;

    @PostConstruct
    public void init() {
        try {
            resolvedUploadPath = Paths.get(uploadPath).toAbsolutePath().normalize();
            Files.createDirectories(resolvedUploadPath);
            log.info("文件上传目录: {}", resolvedUploadPath);
        } catch (IOException e) {
            log.error("创建上传目录失败", e);
        }
    }

    @PostMapping
    @Operation(summary = "上传文件")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                  @RequestParam(defaultValue = "common/") String dir) {
        if (file.isEmpty()) {
            return Result.fail(400, "文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = dir + UUID.randomUUID() + extension;

        try {
            Path targetPath = resolvedUploadPath.resolve(filename);
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath.toFile());
            String url = baseUrl + "/" + filename;
            log.info("文件上传成功: {}", url);
            return Result.ok("上传成功", url);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            return Result.fail(500, "文件上传失败");
        }
    }
}
