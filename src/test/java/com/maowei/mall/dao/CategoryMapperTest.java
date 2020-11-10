package com.maowei.mall.dao;

import com.maowei.mall.MallApplicationTests;
import com.maowei.mall.pojo.Category;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class CategoryMapperTest extends MallApplicationTests {

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    public void findById() {
        Category category = categoryMapper.findById(100001);
        System.out.println(category.toString());
    }

    @Test
    public void queryById() {
        Category category = categoryMapper.queryById(100001);
        System.out.println(category.toString());
    }
}