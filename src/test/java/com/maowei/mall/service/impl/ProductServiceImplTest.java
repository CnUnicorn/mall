package com.maowei.mall.service.impl;

import com.github.pagehelper.PageInfo;
import com.maowei.mall.MallApplicationTests;
import com.maowei.mall.enums.ResponseEnum;
import com.maowei.mall.vo.ProductDetailVo;
import com.maowei.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductServiceImplTest extends MallApplicationTests {

    @Autowired
    private ProductServiceImpl productService;

    @Test
    public void list() {
        ResponseVo<PageInfo> listResponseVo = productService.list(null, 2, 3);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), listResponseVo.getStatus());
    }

    @Test
    public void detail() {
        ResponseVo<ProductDetailVo> detail = productService.detail(26);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), detail.getStatus());
    }
}