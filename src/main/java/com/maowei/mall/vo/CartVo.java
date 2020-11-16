package com.maowei.mall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车模块返回对象
 */
public class CartVo {

    private List<CartProductVo> cartProductVoList;

    /**
     * 是否全选购物车
     */
    private Boolean selectAll;

    /**
     * 购物车商品总价
     */
    private BigDecimal cartTotalPrice;

    /**
     * 购物车商品总数量
     */
    private Integer cartTotalQuantity;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public Boolean getSelectAll() {
        return selectAll;
    }

    public void setSelectAll(Boolean selectAll) {
        this.selectAll = selectAll;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Integer getCartTotalQuantity() {
        return cartTotalQuantity;
    }

    public void setCartTotalQuantity(Integer cartTotalQuantity) {
        this.cartTotalQuantity = cartTotalQuantity;
    }

    @Override
    public String toString() {
        return "CartVo{" +
                "cartProductVoList=" + cartProductVoList +
                ", selectAll=" + selectAll +
                ", cartTotalPrice=" + cartTotalPrice +
                ", cartTotalQuantity=" + cartTotalQuantity +
                '}';
    }
}
