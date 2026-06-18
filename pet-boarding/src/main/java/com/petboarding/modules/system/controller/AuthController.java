package com.petboarding.modules.system.controller;

import com.petboarding.common.result.Result;
import com.petboarding.common.utils.JwtUtils;
import com.petboarding.modules.system.entity.SysUser;
import com.petboarding.modules.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、注册、个人信息")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        // 认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", request.getUsername());
        String token = jwtUtils.generateToken(request.getUsername(), claims);

        // 查询用户信息
        SysUser user = sysUserService.getByUsername(request.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", toUserInfo(user));

        return Result.ok(result);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        SysUser user = sysUserService.register(
                request.getUsername(),
                request.getPassword(),
                request.getRealName(),
                request.getPhone()
        );

        // 注册后自动登录
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        String token = jwtUtils.generateToken(user.getUsername(), claims);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", toUserInfo(user));

        return Result.ok("注册成功", result);
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public Result<Map<String, Object>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser user = sysUserService.getByUsername(authentication.getName());
        return Result.ok(toUserInfo(user));
    }

    @PutMapping("/info")
    @Operation(summary = "更新个人信息")
    public Result<?> updateUserInfo(@RequestBody SysUser user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser currentUser = sysUserService.getByUsername(authentication.getName());
        user.setId(currentUser.getId());
        sysUserService.updateUser(user);
        return Result.ok("更新成功");
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser currentUser = sysUserService.getByUsername(authentication.getName());
        sysUserService.changePassword(currentUser.getId(), request.getOldPassword(), request.getNewPassword());
        return Result.ok("密码修改成功");
    }

    private Map<String, Object> toUserInfo(SysUser user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("realName", user.getRealName());
        info.put("phone", user.getPhone());
        info.put("email", user.getEmail());
        info.put("avatar", user.getAvatar());
        info.put("role", user.getRole());
        info.put("status", user.getStatus());
        return info;
    }

    // ===== 请求体 =====
    @lombok.Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
    }

    @lombok.Data
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
        private String realName;
        private String phone;
    }

    @lombok.Data
    public static class ChangePasswordRequest {
        @NotBlank(message = "旧密码不能为空")
        private String oldPassword;
        @NotBlank(message = "新密码不能为空")
        private String newPassword;
    }
}
