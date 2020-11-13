package com.maowei.mall.service.impl;

import com.maowei.mall.MallApplicationTests;
import com.maowei.mall.enums.ResponseEnum;
import com.maowei.mall.service.ICategoryService;
import com.maowei.mall.vo.CategoryVo;
import com.maowei.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryServiceImplTest extends MallApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImplTest.class);

    @Autowired
    private ICategoryService categoryService;

    @Test
    public void selectAll() {
        ResponseVo<List<CategoryVo>> listResponseVo = categoryService.selectAll();
        for (CategoryVo categoryVo : listResponseVo.getData()) {
            System.out.println(categoryVo.toString());
        }
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), listResponseVo.getStatus());
    }

    @Test
    public void findSubCategoryId() {
        Set<Integer> resultId = new HashSet<>();
        categoryService.findSubCategoryId(100001, resultId);
        logger.info("set={}", resultId);
    }
}