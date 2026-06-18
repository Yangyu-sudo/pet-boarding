package com.petboarding.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petboarding.common.result.Result;
import com.petboarding.modules.system.entity.SysUser;
import com.petboarding.modules.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "系统用户CRUD（管理员）")
@PreAuthorize("hasRole('ADMIN')")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @Operation(summary = "用户列表")
    public Result<Page<SysUser>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword)
                    .or().like(SysUser::getPhone, keyword));
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(SysUser::getRole, role);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result = sysUserService.page(new Page<>(page, size), wrapper);
        // 清除密码字段
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "用户详情")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.ok(user);
    }

    @PostMapping
    @Operation(summary = "添加用户")
    public Result<?> add(@RequestBody SysUser user) {
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        sysUserService.save(user);
        return Result.ok("添加成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑用户")
    public Result<?> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        // 如果密码不为空，则更新密码
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        sysUserService.updateById(user);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<?> delete(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.ok("删除成功");
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        sysUserService.updateById(user);
        return Result.ok("状态更新成功");
    }
}
