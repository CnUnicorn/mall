package com.maowei.mall.listener;

import com.google.gson.Gson;
import com.maowei.mall.pojo.PayInfo;
import com.maowei.mall.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 关于PayInfo对象，pay项目应该提供client.jar，mall项目引入jar包，而不需要自己新建对象
 */
@Component
@Slf4j
@RabbitListener(queues = "payNotify")
public class PayMsgListener {

    @Autowired
    private IOrderService orderService;

    @RabbitHandler
    public void process(String msg) {
        log.info("接收到的消息={}", msg);
        PayInfo payInfo = new Gson().fromJson(msg, PayInfo.class);
        if (payInfo.getPlatformStatus().equals("SUCCESS")) {
            // 修改订单里的状态
            orderService.paid(payInfo.getOrderNo());
        }

    }

}
