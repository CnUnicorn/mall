package com.maowei.mall.service.impl;

import com.maowei.mall.MallApplicationTests;
import com.maowei.mall.enums.ResponseEnum;
import com.maowei.mall.enums.RoleEnum;
import com.maowei.mall.pojo.User;
import com.maowei.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImplTest extends MallApplicationTests {

    private static final String USERNAME = "jack";

    private static final String PASSWORD = "jack";

    @Autowired
    private UserServiceImpl userService;

    @Before
    public void register() {
        User user = new User(USERNAME, PASSWORD, "john@163.com", RoleEnum.CUSTOMER.getCode());
        userService.register(user);
    }

    @Test
    public void login() {
        ResponseVo<User> responseVo = userService.login(USERNAME, PASSWORD);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());

    }
}