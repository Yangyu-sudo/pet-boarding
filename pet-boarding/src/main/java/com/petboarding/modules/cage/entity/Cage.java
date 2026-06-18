package com.petboarding.modules.cage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cage")
public class Cage implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String cageNo;

    private String type;

    private String size;

    private String status;

    private BigDecimal dailyPrice;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
