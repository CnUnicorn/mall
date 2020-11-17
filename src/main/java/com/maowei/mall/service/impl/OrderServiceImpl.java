package com.maowei.mall.service.impl;

import com.maowei.mall.service.IOrderService;
import com.maowei.mall.vo.OrderVo;
import com.maowei.mall.vo.ResponseVo;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements IOrderService {

    /**
     *
     * @param uid 用户id
     * @param shippingId 收货地址id，shipping表中的主键
     * @return
     */
    @Override
    public ResponseVo<OrderVo> create(Integer uid, Integer shippingId) {
        // 收货地址校验（最后要返回收获地址，一定会从数据库中查出来）

        // 获取购物车，校验（是否有商品、库存是否充足）

        // 计算总价，只计算选中的商品

        // 生成订单，入库：Order表，OrderItem表，事务，保证两个表数据都同时写入了，不会出现数据不同步的情况

        // 减库存

        // 更新购物车（删掉选中的商品）

        // 构造OrderVo对象

        return null;
    }
}
