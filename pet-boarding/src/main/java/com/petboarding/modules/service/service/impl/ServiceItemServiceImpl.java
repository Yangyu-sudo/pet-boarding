package com.petboarding.modules.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petboarding.modules.service.entity.ServiceItem;
import com.petboarding.modules.service.mapper.ServiceItemMapper;
import com.petboarding.modules.service.service.ServiceItemService;
import org.springframework.stereotype.Service;

@Service
public class ServiceItemServiceImpl extends ServiceImpl<ServiceItemMapper, ServiceItem> implements ServiceItemService {
}
