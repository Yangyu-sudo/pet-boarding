package com.petboarding.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "没有操作权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 业务错误码 1xxx
    USERNAME_EXISTS(1001, "用户名已存在"),
    USER_NOT_FOUND(1002, "用户不存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    ACCOUNT_DISABLED(1004, "账号已被禁用"),
    TOKEN_EXPIRED(1005, "Token已过期"),
    TOKEN_INVALID(1006, "Token无效"),

    // 业务错误码 2xxx
    PET_NOT_FOUND(2001, "宠物不存在"),
    CAGE_NOT_AVAILABLE(2002, "笼舍不可用"),
    ORDER_NOT_FOUND(2003, "订单不存在"),
    ORDER_STATUS_ERROR(2004, "订单状态不正确"),
    DATE_RANGE_ERROR(2005, "日期范围错误"),
    SERVICE_NOT_FOUND(2006, "服务项目不存在"),

    // 业务错误码 3xxx
    FILE_UPLOAD_ERROR(3001, "文件上传失败"),
    FILE_FORMAT_ERROR(3002, "文件格式不支持");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
