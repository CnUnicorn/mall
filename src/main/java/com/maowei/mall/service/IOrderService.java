package com.maowei.mall.service;

import com.maowei.mall.vo.OrderVo;
import com.maowei.mall.vo.ResponseVo;

public interface IOrderService {

    ResponseVo<OrderVo> create(Integer uid, Integer shippingId);
}
