package com.maowei.mall.enums;

public enum ResponseEnum {

    ERROR(-1, "服务端错误"), // 最宽泛的错误，使用-1

    SUCCESS(0, "成功"),

    PASSWORD_ERROR(1, "密码错误"),

    USERNAME_EXIST(2, "用户名已存在"),

    EMAIL_EXIST(4, "邮箱已存在"),

    PARAM_ERROR(3, "参数错误"),

    NEED_LOGIN(10, "用户未登录，请先登录"),
    ;

    Integer code;

    String desc;

    ResponseEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getCode() {
        return code;
    }
}
