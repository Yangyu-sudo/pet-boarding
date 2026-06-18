package com.petboarding.modules.record.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.common.result.Result;
import com.petboarding.modules.record.entity.DailyRecord;
import com.petboarding.modules.record.service.DailyRecordService;
import com.petboarding.modules.system.entity.SysUser;
import com.petboarding.modules.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@Tag(name = "每日记录", description = "宠物每日护理记录")
public class DailyRecordController {

    @Autowired
    private DailyRecordService dailyRecordService;

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/order/{orderId}")
    @Operation(summary = "获取订单的每日记录")
    public Result<List<DailyRecord>> listByOrder(@PathVariable Long orderId) {
        List<DailyRecord> records = dailyRecordService.list(
                new LambdaQueryWrapper<DailyRecord>()
                        .eq(DailyRecord::getOrderId, orderId)
                        .orderByDesc(DailyRecord::getRecordDate)
        );
        return Result.ok(records);
    }

    @PostMapping
    @Operation(summary = "添加每日记录")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> add(@RequestBody DailyRecord record) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SysUser currentUser = sysUserService.getByUsername(auth.getName());
        record.setStaffId(currentUser.getId());
        dailyRecordService.save(record);
        return Result.ok("记录添加成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新每日记录")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> update(@PathVariable Long id, @RequestBody DailyRecord record) {
        record.setId(id);
        dailyRecordService.updateById(record);
        return Result.ok("更新成功");
    }
}
