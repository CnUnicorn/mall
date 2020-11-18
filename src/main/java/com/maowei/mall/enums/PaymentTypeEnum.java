package com.maowei.mall.enums;

/**
 * 支付类型,1-在线支付
 */
public enum PaymentTypeEnum {

    PAY_ONLINE(1),

    ;

    Integer code;

    PaymentTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
