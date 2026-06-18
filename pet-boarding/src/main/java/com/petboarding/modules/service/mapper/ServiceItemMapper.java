package com.petboarding.modules.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petboarding.modules.service.entity.ServiceItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServiceItemMapper extends BaseMapper<ServiceItem> {
}
