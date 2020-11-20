package com.maowei.mall.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maowei.mall.MallApplicationTests;
import com.maowei.mall.enums.ResponseEnum;
import com.maowei.mall.form.CartAddForm;
import com.maowei.mall.service.ICartService;
import com.maowei.mall.service.IOrderService;
import com.maowei.mall.vo.OrderVo;
import com.maowei.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OrderServiceImplTest extends MallApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImplTest.class);

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ICartService cartService;

    private Integer uid = 1;

    private Integer shippingId = 6;

    private Integer productId = 26;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Before
    public void before() {
        CartAddForm cartAddForm = new CartAddForm();
        cartAddForm.setProductId(productId);
        cartAddForm.setSelected(true);
        cartService.add(uid, cartAddForm);
    }

    @Test
    public void create() {
        ResponseVo<OrderVo> responseVo = orderService.create(uid, shippingId);
        logger.info("responseVo={}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());

    }

    @Test
    public void list() {
        ResponseVo<PageInfo> responseVo = orderService.list(uid, 1, 2);
        logger.info("responseVo={}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    public void detail() {
        Long orderNo = 1605681426562L;
        ResponseVo<OrderVo> responseVo = orderService.detail(uid, orderNo);
        logger.info("responseVo={}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    public void cancel() {
        Long orderNo = 1605681426562L;
        ResponseVo responseVo = orderService.cancel(uid, orderNo);
        logger.info("responseVo={}", gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }
}