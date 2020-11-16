package com.maowei.mall.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maowei.mall.MallApplicationTests;
import com.maowei.mall.form.CartAddForm;
import com.maowei.mall.form.CartUpdateForm;
import com.maowei.mall.service.ICartService;
import com.maowei.mall.vo.CartVo;
import com.maowei.mall.vo.ResponseVo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CartServiceImplTest extends MallApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImplTest.class);

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private ICartService cartService;

    private Integer uid = 1;

    private Integer productId = 26;

    @Test
    public void add() {
        CartAddForm cartAddForm = new CartAddForm();
        cartAddForm.setProductId(productId);
        cartAddForm.setSelected(true);
        cartService.add(uid, cartAddForm);
    }

    @Test
    public void list() {
        ResponseVo<CartVo> list = cartService.list(uid);
        logger.info("list={}", gson.toJson(list));
    }

    @Test
    public void update() {
        CartUpdateForm cartUpdateForm = new CartUpdateForm();
        cartUpdateForm.setQuantity(10);
        cartUpdateForm.setSelected(true);

        ResponseVo<CartVo> list = cartService.update(uid, productId, cartUpdateForm);
        logger.info("list={}", gson.toJson(list));

    }

    @Test
    public void delete() {
        ResponseVo<CartVo> list = cartService.delete(uid, productId);
        logger.info("list={}", gson.toJson(list));
    }

    @Test
    public void selectAll() {
        ResponseVo<CartVo> list = cartService.selectAll(uid);
        logger.info("list={}", gson.toJson(list));
    }

    @Test
    public void unSelectAll() {
        ResponseVo<CartVo> list = cartService.unSelectAll(uid);
        logger.info("list={}", gson.toJson(list));
    }

    @Test
    public void sum() {
        ResponseVo<Integer> sum = cartService.sum(uid);
        logger.info("sum={}", gson.toJson(sum));
    }
}