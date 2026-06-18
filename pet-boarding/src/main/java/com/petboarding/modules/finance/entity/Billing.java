package com.petboarding.modules.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("billing")
public class Billing implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private BigDecimal totalAmount;

    private BigDecimal deposit;

    private BigDecimal additionalCharges;

    private BigDecimal finalAmount;

    private String paymentStatus;

    private String paymentMethod;

    private LocalDateTime paymentTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
