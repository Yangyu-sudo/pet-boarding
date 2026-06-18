package com.petboarding.modules.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("boarding_order")
public class BoardingOrder implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long petId;

    private Long ownerId;

    private Long cageId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private String status;

    private BigDecimal totalPrice;

    private BigDecimal deposit;

    private String specialRequirements;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 非数据库字段 - 关联查询
    @TableField(exist = false)
    private String petName;

    @TableField(exist = false)
    private String ownerName;

    @TableField(exist = false)
    private String cageNo;

    @TableField(exist = false)
    private String petType;

    @TableField(exist = false)
    private List<OrderServiceItem> services;
}
