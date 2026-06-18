package com.petboarding.modules.cage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petboarding.common.exception.BusinessException;
import com.petboarding.common.result.Result;
import com.petboarding.common.result.ResultCode;
import com.petboarding.modules.cage.entity.Cage;
import com.petboarding.modules.cage.service.CageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cages")
@Tag(name = "笼舍管理", description = "笼舍CRUD和状态管理")
public class CageController {

    @Autowired
    private CageService cageService;

    @GetMapping
    @Operation(summary = "笼舍列表")
    public Result<Page<Cage>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {

        LambdaQueryWrapper<Cage> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) {
            wrapper.eq(Cage::getType, type);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Cage::getStatus, status);
        }
        wrapper.orderByAsc(Cage::getCageNo);

        return Result.ok(cageService.page(new Page<>(page, size), wrapper));
    }

    @GetMapping("/{id}")
    @Operation(summary = "笼舍详情")
    public Result<Cage> getById(@PathVariable Long id) {
        return Result.ok(cageService.getById(id));
    }

    @PostMapping
    @Operation(summary = "添加笼舍")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<?> add(@RequestBody Cage cage) {
        cageService.save(cage);
        return Result.ok("添加成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新笼舍")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> update(@PathVariable Long id, @RequestBody Cage cage) {
        cage.setId(id);
        cageService.updateById(cage);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除笼舍")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        cageService.removeById(id);
        return Result.ok("删除成功");
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新笼舍状态")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        Cage cage = cageService.getById(id);
        if (cage == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "笼舍不存在");
        }
        Cage update = new Cage();
        update.setId(id);
        update.setStatus(status);
        cageService.updateById(update);
        return Result.ok("状态更新成功");
    }
}
