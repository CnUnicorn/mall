package com.maowei.mall.service;

import com.github.pagehelper.PageInfo;
import com.maowei.mall.vo.ProductDetailVo;
import com.maowei.mall.vo.ResponseVo;

public interface IProductService {

    /**
     * 返回某种商品及其子类别商品的列表
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize);

    ResponseVo<ProductDetailVo> detail(Integer productId);
}
