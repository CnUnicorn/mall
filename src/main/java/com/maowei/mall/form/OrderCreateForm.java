package com.maowei.mall.form;

import javax.validation.constraints.NotNull;

public class OrderCreateForm {

    @NotNull
    private Integer shippingId;

    public Integer getShippingId() {
        return shippingId;
    }

    public void setShippingId(Integer shippingId) {
        this.shippingId = shippingId;
    }
}
