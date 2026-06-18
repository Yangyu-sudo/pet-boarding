package com.petboarding.common.controller;

import com.petboarding.common.result.Result;
import com.petboarding.common.utils.OssUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "文件上传", description = "图片/文件上传")
public class FileUploadController {

    @Autowired
    private OssUtils ossUtils;

    @PostMapping
    @Operation(summary = "上传文件")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                 @RequestParam(defaultValue = "common/") String dir) {
        String url = ossUtils.upload(file, dir);
        return Result.ok("上传成功", url);
    }
}
