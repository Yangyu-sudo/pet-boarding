package com.petboarding.modules.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petboarding.common.result.Result;
import com.petboarding.modules.service.entity.ServiceItem;
import com.petboarding.modules.service.service.ServiceItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/services")
@Tag(name = "服务管理", description = "服务项目CRUD")
public class ServiceItemController {

    @Autowired
    private ServiceItemService serviceItemService;

    @GetMapping
    @Operation(summary = "服务项目列表")
    public Result<Page<ServiceItem>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ServiceItem::getPrice);
        return Result.ok(serviceItemService.page(new Page<>(page, size), wrapper));
    }

    @GetMapping("/{id}")
    @Operation(summary = "服务项目详情")
    public Result<ServiceItem> getById(@PathVariable Long id) {
        return Result.ok(serviceItemService.getById(id));
    }

    @PostMapping
    @Operation(summary = "添加服务项目")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> add(@RequestBody ServiceItem serviceItem) {
        serviceItemService.save(serviceItem);
        return Result.ok("添加成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新服务项目")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> update(@PathVariable Long id, @RequestBody ServiceItem serviceItem) {
        serviceItem.setId(id);
        serviceItemService.updateById(serviceItem);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除服务项目")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        serviceItemService.removeById(id);
        return Result.ok("删除成功");
    }
}
