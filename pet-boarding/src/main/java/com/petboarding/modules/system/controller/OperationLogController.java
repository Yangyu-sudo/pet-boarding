package com.petboarding.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petboarding.common.result.Result;
import com.petboarding.modules.system.entity.OperationLog;
import com.petboarding.modules.system.mapper.OperationLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "操作日志", description = "系统操作日志查询")
@PreAuthorize("hasRole('ADMIN')")
public class OperationLogController {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @GetMapping
    @Operation(summary = "操作日志列表")
    public Result<Page<OperationLog>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return Result.ok(new Page<OperationLog>(page, size).setRecords(
                operationLogMapper.selectPage(new Page<>(page, size), wrapper).getRecords()
        ));
    }
}
