package com.maowei.mall.form;

import javax.validation.constraints.NotNull;

/**
 * 添加商品表单
 */
public class CartAddForm {

    @NotNull
    private Integer productId;

    private Boolean selected = true;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
