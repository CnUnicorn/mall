package com.maowei.mall.service;

import com.maowei.mall.vo.CategoryVo;
import com.maowei.mall.vo.ResponseVo;

import java.util.List;
import java.util.Set;

public interface ICategoryService {

    ResponseVo<List<CategoryVo>> selectAll();

    void findSubCategoryId(Integer id, Set<Integer> resultSet);
}
