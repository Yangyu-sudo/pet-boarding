package com.petboarding.modules.cage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petboarding.modules.cage.entity.Cage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CageMapper extends BaseMapper<Cage> {
}
