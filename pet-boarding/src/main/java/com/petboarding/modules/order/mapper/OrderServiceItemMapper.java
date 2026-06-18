package com.petboarding.modules.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petboarding.modules.order.entity.OrderServiceItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderServiceItemMapper extends BaseMapper<OrderServiceItem> {
}
