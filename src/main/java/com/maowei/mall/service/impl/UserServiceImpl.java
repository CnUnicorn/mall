package com.maowei.mall.service.impl;

import com.maowei.mall.dao.UserMapper;
import com.maowei.mall.enums.ResponseEnum;
import com.maowei.mall.enums.RoleEnum;
import com.maowei.mall.pojo.User;
import com.maowei.mall.service.IUserService;
import com.maowei.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.Charset;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;  // 查询当前数据库中的用户

    /**
     * 注册
     * @param user 根据文档设置的api，request中的User只有username，password，email有值
     */
    @Override
    public ResponseVo register(User user) {
        // 校验username，不能重复
        int countByUsername = userMapper.countByUsername(user.getUsername());
        if (countByUsername > 0) {
            return ResponseVo.error(ResponseEnum.USERNAME_EXIST);
        }

        // 校验email，不能重复
        int countByEmail = userMapper.countByEmail(user.getEmail());
        if (countByEmail > 0) {
            return ResponseVo.error(ResponseEnum.EMAIL_EXIST);
        }

        user.setRole(RoleEnum.CUSTOMER.getCode()); // 默认注册为普通用户

        //MD5摘要算法（spring自带），密码加密
        user.setPassword(
                DigestUtils.md5DigestAsHex(user.getUsername().getBytes(Charset.forName("utf-8")))
        );

        // 写入数据库
        int insertSelective = userMapper.insertSelective(user); // 由于User有部分值缺失，使用insertSelective
        if (insertSelective == 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        return ResponseVo.success();
    }

    @Override
    public ResponseVo<User> login(String username, String password) {
        User user = userMapper.selectByUsername(username); // 根据用户名查找数据库中的用户信息
        // 考虑安全性，统一返回用户名或密码错误，让用户无法知道数据库是否有某个用户
        if (user == null) {
            // 用户不存在
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);

        }
        // 比较输入密码和数据库中的密码是否相同
        if (!user.getPassword().equalsIgnoreCase(
                DigestUtils.md5DigestAsHex(password.getBytes(Charset.forName("utf-8"))))){
            // 密码错误
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }

        user.setPassword(""); // 返回的时候密码设置为空
        return ResponseVo.success(user);
    }
}
