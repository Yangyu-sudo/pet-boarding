package com.petboarding.modules.record.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("daily_record")
public class DailyRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private LocalDate recordDate;

    private String feedingStatus;

    private String healthStatus;

    private String mood;

    private String notes;

    private String photos;

    private Long staffId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String staffName;
}
