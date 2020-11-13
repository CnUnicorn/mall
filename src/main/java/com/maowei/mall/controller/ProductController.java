package com.maowei.mall.controller;

import com.github.pagehelper.PageInfo;
import com.maowei.mall.service.IProductService;
import com.maowei.mall.vo.ProductDetailVo;
import com.maowei.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping("/products")
    public ResponseVo<PageInfo> getProductByCategoryId(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                       @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                       @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return productService.list(categoryId, pageNum, pageSize);
    }

    @GetMapping("/products/{productId}")
    public ResponseVo<ProductDetailVo> detail(@PathVariable(value = "productId") Integer productId) {
        return productService.detail(productId);
    }
}
