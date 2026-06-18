package com.petboarding.common.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.modules.system.entity.SysUser;
import com.petboarding.modules.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
        );

        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (sysUser.getStatus() == 0) {
            throw new UsernameNotFoundException("账号已被禁用: " + username);
        }

        return new User(
                sysUser.getUsername(),
                sysUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + sysUser.getRole()))
        );
    }
}
