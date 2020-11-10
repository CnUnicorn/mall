package com.maowei.mall.controller;

import com.maowei.mall.enums.ResponseEnum;
import com.maowei.mall.form.UserForm;
import com.maowei.mall.pojo.User;
import com.maowei.mall.service.IUserService;
import com.maowei.mall.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseVo register(@Valid @RequestBody UserForm userForm,
                               BindingResult bindingResult) {
        // 保证输入格式正确
        if (bindingResult.hasErrors()) {
            logger.info("注册提交的参数有误，{} {}",
                    bindingResult.getFieldError().getField(),
                    bindingResult.getFieldError().getDefaultMessage());
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, bindingResult);
        }

        User user = new User();
        BeanUtils.copyProperties(userForm, user); // 拷贝属性
        return userService.register(user);
    }
}
