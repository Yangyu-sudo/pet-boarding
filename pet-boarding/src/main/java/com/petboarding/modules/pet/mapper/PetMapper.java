package com.petboarding.modules.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petboarding.modules.pet.entity.Pet;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PetMapper extends BaseMapper<Pet> {
}
