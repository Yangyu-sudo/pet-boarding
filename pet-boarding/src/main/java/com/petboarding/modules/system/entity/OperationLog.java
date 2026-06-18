package com.petboarding.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private String module;

    private String operation;

    private String description;

    private String method;

    private String params;

    private String ip;

    private Long duration;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
