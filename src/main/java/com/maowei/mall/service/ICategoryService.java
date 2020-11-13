package com.maowei.mall.service;

import com.maowei.mall.vo.CategoryVo;
import com.maowei.mall.vo.ResponseVo;

import java.util.List;

public interface ICategoryService {

    ResponseVo<List<CategoryVo>> selectAll();
}
