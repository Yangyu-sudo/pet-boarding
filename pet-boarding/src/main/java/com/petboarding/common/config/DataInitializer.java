package com.petboarding.common.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.modules.cage.entity.Cage;
import com.petboarding.modules.cage.mapper.CageMapper;
import com.petboarding.modules.service.entity.ServiceItem;
import com.petboarding.modules.service.mapper.ServiceItemMapper;
import com.petboarding.modules.system.entity.SysUser;
import com.petboarding.modules.system.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 数据初始化器 — 首次启动时自动创建演示账号和基础数据
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private CageMapper cageMapper;

    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initUsers();
        initCages();
        initServices();
    }

    private void initUsers() {
        if (sysUserMapper.selectCount(new LambdaQueryWrapper<>()) > 0) {
            log.info("用户数据已存在，跳过初始化");
            return;
        }

        String encodedPassword = passwordEncoder.encode("123456");
        log.info("========================================");
        log.info("演示账号密码均为: 123456");
        log.info("BCrypt密文: {}", encodedPassword);
        log.info("========================================");

        SysUser admin = new SysUser();
        admin.setUsername("admin");
        admin.setPassword(encodedPassword);
        admin.setRealName("系统管理员");
        admin.setPhone("13800000000");
        admin.setEmail("admin@pet.com");
        admin.setRole("ADMIN");
        admin.setStatus(1);
        sysUserMapper.insert(admin);

        SysUser staff = new SysUser();
        staff.setUsername("staff01");
        staff.setPassword(encodedPassword);
        staff.setRealName("张三");
        staff.setPhone("13800000001");
        staff.setEmail("staff01@pet.com");
        staff.setRole("STAFF");
        staff.setStatus(1);
        sysUserMapper.insert(staff);

        SysUser customer = new SysUser();
        customer.setUsername("cust01");
        customer.setPassword(encodedPassword);
        customer.setRealName("李四");
        customer.setPhone("13800000002");
        customer.setEmail("cust01@pet.com");
        customer.setRole("CUSTOMER");
        customer.setStatus(1);
        sysUserMapper.insert(customer);

        log.info("演示用户初始化完成: admin, staff01, cust01");
    }

    private void initCages() {
        if (cageMapper.selectCount(new LambdaQueryWrapper<>()) > 0) {
            log.info("笼舍数据已存在，跳过初始化");
            return;
        }

        String[][] cageData = {
            {"S001", "STANDARD", "1.5m×1.5m", "80.00", "标准笼舍，适合小型宠物"},
            {"S002", "STANDARD", "1.5m×1.5m", "80.00", "标准笼舍，适合小型宠物"},
            {"D001", "DELUXE", "2m×2m", "150.00", "豪华笼舍，独立空调，适合中型宠物"},
            {"D002", "DELUXE", "2m×2m", "150.00", "豪华笼舍，独立空调，适合中型宠物"},
            {"V001", "VIP", "3m×3m", "300.00", "VIP套房，独立小院，24小时监控"},
            {"V002", "VIP", "3m×3m", "300.00", "VIP套房，独立小院，24小时监控"},
        };

        for (String[] d : cageData) {
            Cage cage = new Cage();
            cage.setCageNo(d[0]);
            cage.setType(d[1]);
            cage.setSize(d[2]);
            cage.setDailyPrice(new BigDecimal(d[3]));
            cage.setDescription(d[4]);
            cage.setStatus("AVAILABLE");
            cageMapper.insert(cage);
        }

        log.info("笼舍数据初始化完成: {} 个", cageData.length);
    }

    private void initServices() {
        if (serviceItemMapper.selectCount(new LambdaQueryWrapper<>()) > 0) {
            log.info("服务数据已存在，跳过初始化");
            return;
        }

        String[][] serviceData = {
            {"洗澡美容", "宠物洗澡+基础美容造型", "120.00", "60"},
            {"健康体检", "基础健康检查+血常规", "200.00", "30"},
            {"遛狗服务", "每日遛狗30分钟", "30.00", "30"},
            {"行为训练", "基础服从训练课程", "150.00", "45"},
            {"药浴护理", "皮肤病药浴护理", "80.00", "40"},
        };

        for (String[] d : serviceData) {
            ServiceItem item = new ServiceItem();
            item.setName(d[0]);
            item.setDescription(d[1]);
            item.setPrice(new BigDecimal(d[2]));
            item.setDuration(Integer.parseInt(d[3]));
            item.setStatus(1);
            serviceItemMapper.insert(item);
        }

        log.info("服务数据初始化完成: {} 个", serviceData.length);
    }
}
