package com.maowei.mall.pojo;

/**
 * 从Redis中查出来的对象
 */
public class Cart {

    private Integer productId;

    private Integer quantity;

    private Boolean productSelected;

    public Cart() {
    }

    public Cart(Integer productId, Integer quantity, Boolean productSelected) {
        this.productId = productId;
        this.quantity = quantity;
        this.productSelected = productSelected;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getProductSelected() {
        return productSelected;
    }

    public void setProductSelected(Boolean productSelected) {
        this.productSelected = productSelected;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", productSelected=" + productSelected +
                '}';
    }
}
