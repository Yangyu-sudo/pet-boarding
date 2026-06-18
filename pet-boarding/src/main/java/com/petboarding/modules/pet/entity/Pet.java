package com.petboarding.modules.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pet")
public class Pet implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String type;

    private String breed;

    private Integer age;

    private String gender;

    private BigDecimal weight;

    private Long ownerId;

    private String photo;

    private String medicalHistory;

    private String notes;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
