package com.petboarding.modules.cage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petboarding.modules.cage.entity.Cage;
import com.petboarding.modules.cage.mapper.CageMapper;
import com.petboarding.modules.cage.service.CageService;
import org.springframework.stereotype.Service;

@Service
public class CageServiceImpl extends ServiceImpl<CageMapper, Cage> implements CageService {
}
