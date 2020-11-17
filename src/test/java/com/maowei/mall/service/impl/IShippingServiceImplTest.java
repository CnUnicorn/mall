package com.maowei.mall.service.impl;

import com.github.pagehelper.PageInfo;
import com.maowei.mall.MallApplicationTests;
import com.maowei.mall.enums.ResponseEnum;
import com.maowei.mall.form.ShippingForm;
import com.maowei.mall.service.IShippingService;
import com.maowei.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class IShippingServiceImplTest extends MallApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(IShippingServiceImplTest.class);

    @Autowired
    private IShippingService shippingService;

    private Integer uid = 1;

    @Test
    public void add() {
        ShippingForm form = new ShippingForm();
        form.setReceiverName("徐茂蔚");
        form.setReceiverPhone("15611519733");
        form.setReceiverProvince("北京");
        form.setReceiverCity("北京");
        form.setReceiverDistrict("海淀区");
        form.setReceiverAddress("北京邮电大学");
        ResponseVo<Map<String, Integer>> add = shippingService.add(uid, form);
        logger.info("result={}", add);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), add.getStatus());
    }

    @Test
    public void delete() {
        Integer shippingId = 5;
        ResponseVo delete = shippingService.delete(shippingId, uid);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), delete.getStatus());
    }

    @Test
    public void update() {
        ShippingForm form = new ShippingForm();
        form.setReceiverName("xmw");
        form.setReceiverPhone("15611519733");
        form.setReceiverProvince("北京");
        form.setReceiverCity("北京");
        form.setReceiverDistrict("海淀区");
        form.setReceiverAddress("北京邮电大学");
        Integer shippingId = 6;
        ResponseVo update = shippingService.update(uid, shippingId, form);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), update.getStatus());

    }

    @Test
    public void list() {
        ResponseVo<PageInfo> list = shippingService.list(uid, 1, 10);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), list.getStatus());

    }
}