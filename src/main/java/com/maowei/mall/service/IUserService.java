package com.maowei.mall.service;

import com.maowei.mall.pojo.User;
import com.maowei.mall.vo.ResponseVo;

public interface IUserService {

    /**
     * 注册
     */
    ResponseVo register(User user);

    /**
     * 登录
     */
}
