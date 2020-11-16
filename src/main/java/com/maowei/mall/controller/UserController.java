package com.maowei.mall.controller;

import com.maowei.mall.consts.MallConst;
import com.maowei.mall.form.UserLoginForm;
import com.maowei.mall.form.UserRegisterForm;
import com.maowei.mall.pojo.User;
import com.maowei.mall.service.IUserService;
import com.maowei.mall.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private IUserService userService;

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/user/register")
    public ResponseVo<User> register(@Valid @RequestBody UserRegisterForm userRegisterForm) {
        // 保证输入格式正确，使用统一异常处理，因为有很多地方需要保证输入格式的正确性
//        if (bindingResult.hasErrors()) {
//            logger.info("注册提交的参数有误，{} {}",
//                    bindingResult.getFieldError().getField(),
//                    bindingResult.getFieldError().getDefaultMessage());
//            return ResponseVo.error(ResponseEnum.PARAM_ERROR, bindingResult);
//        }

        User user = new User();
        BeanUtils.copyProperties(userRegisterForm, user); // 拷贝属性
        return userService.register(user);
    }

    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm,
                                  HttpServletRequest httpServletRequest) {
        // 保证输入格式正确，和register一样，交由统一异常处理
        ResponseVo<User> responseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());

        // 设置Session
        HttpSession session = httpServletRequest.getSession(); // 通过参数中的HttpServletRequest来得到Session
        // 也可以直接将参数声明成HttpSession来直接使用Session变量
        session.setAttribute(MallConst.CURRENT_USER, responseVo.getData());
        logger.info("/login sessionId={}", session.getId());

        return responseVo;
    }

    // Session保存在内存里，服务器重启或者项目重启信息就丢失了
    // 改进版本token（SessionId）+redis
    @GetMapping("/user")
    public ResponseVo<User> userInfo(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        logger.info("/user sessionId={}", session.getId());
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return ResponseVo.success(user);
    }

    @PostMapping("/user/logout")
    /**
     * {@link TomcatServletWebServerFactory} getSessionTimeoutInMinutes方法限制了最短失效时间为1分钟
     */
    public ResponseVo<User> logout(HttpSession session) { // 可以直接得到HttpSession，代替HttpServletRequest
        logger.info("/logout sessionId={}", session.getId());
        session.removeAttribute(MallConst.CURRENT_USER);
        return ResponseVo.success();
    }
}
