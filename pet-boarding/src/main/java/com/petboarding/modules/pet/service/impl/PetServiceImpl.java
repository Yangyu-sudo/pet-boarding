package com.petboarding.modules.pet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petboarding.modules.pet.entity.Pet;
import com.petboarding.modules.pet.mapper.PetMapper;
import com.petboarding.modules.pet.service.PetService;
import org.springframework.stereotype.Service;

@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {
}
