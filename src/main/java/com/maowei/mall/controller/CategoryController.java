package com.maowei.mall.controller;

import com.maowei.mall.service.ICategoryService;
import com.maowei.mall.vo.CategoryVo;
import com.maowei.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/categories")
    public ResponseVo<List<CategoryVo>> getAllCategories() {
        return categoryService.selectAll();
    }
}
