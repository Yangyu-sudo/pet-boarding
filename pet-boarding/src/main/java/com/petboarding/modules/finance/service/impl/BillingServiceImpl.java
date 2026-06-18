package com.petboarding.modules.finance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petboarding.modules.finance.entity.Billing;
import com.petboarding.modules.finance.mapper.BillingMapper;
import com.petboarding.modules.finance.service.BillingService;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceImpl extends ServiceImpl<BillingMapper, Billing> implements BillingService {
}
