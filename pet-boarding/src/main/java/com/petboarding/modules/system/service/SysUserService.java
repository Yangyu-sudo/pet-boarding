package com.petboarding.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petboarding.modules.system.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

    /**
     * 用户注册
     */
    SysUser register(String username, String password, String realName, String phone);

    /**
     * 根据用户名查询
     */
    SysUser getByUsername(String username);

    /**
     * 更新用户信息
     */
    void updateUser(SysUser user);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
}
