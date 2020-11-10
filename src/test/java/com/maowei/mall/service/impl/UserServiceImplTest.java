package com.maowei.mall.service.impl;

import com.maowei.mall.MallApplicationTests;
import com.maowei.mall.enums.RoleEnum;
import com.maowei.mall.pojo.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImplTest extends MallApplicationTests {

    @Autowired
    private UserServiceImpl userService;

    @Test
    public void register() {
        User user = new User("john", "123465", "john@163.com", RoleEnum.CUSTOMER.getCode());
        userService.register(user);
//        userService.register(user);
    }
}