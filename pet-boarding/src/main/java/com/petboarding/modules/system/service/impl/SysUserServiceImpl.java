package com.petboarding.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petboarding.common.exception.BusinessException;
import com.petboarding.common.result.ResultCode;
import com.petboarding.modules.system.entity.SysUser;
import com.petboarding.modules.system.mapper.SysUserMapper;
import com.petboarding.modules.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SysUser register(String username, String password, String realName, String phone) {
        // 检查用户名是否已存在
        SysUser existUser = getByUsername(username);
        if (existUser != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(realName);
        user.setPhone(phone);
        user.setRole("CUSTOMER");
        user.setStatus(1);

        save(user);
        return user;
    }

    @Override
    public SysUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public void updateUser(SysUser user) {
        // 不允许修改用户名和角色
        user.setUsername(null);
        user.setPassword(null);
        user.setRole(null);
        updateById(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }
}
