package com.maowei.mall.service;

import com.maowei.mall.form.CartAddForm;
import com.maowei.mall.form.CartUpdateForm;
import com.maowei.mall.vo.CartVo;
import com.maowei.mall.vo.ResponseVo;

public interface ICartService {

    ResponseVo<CartVo> add(Integer uid, CartAddForm cartAddForm);

    /**
     * 返回redis某张表内的所有商品的数据，从api文档中可以看到，很多接口都需要返回购物车所有商品
     * 所以，把这个返回列表的操作提取出来，单独列为一个接口，每次需要的时候就调用这个接口
     * @param uid
     * @return
     */
    ResponseVo<CartVo> list(Integer uid);

    ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm cartUpdateForm);

    ResponseVo<CartVo> delete(Integer uid, Integer productId);

    ResponseVo<CartVo> selectAll(Integer uid);

    ResponseVo<CartVo> unSelectAll(Integer uid);

    ResponseVo<Integer> sum(Integer uid);

}
