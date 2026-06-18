package com.petboarding.modules.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName("order_service")
public class OrderServiceItem implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long serviceId;

    private Integer quantity;

    private BigDecimal price;

    // 非数据库字段
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String serviceName;
}
