package com.maowei.mall.interceptor;

import com.maowei.mall.consts.MallConst;
import com.maowei.mall.exception.UserLoginException;
import com.maowei.mall.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserLoginInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginInterceptor.class);

    /**
     * 对登录状态进行判断，如果用户未登录，抛出异常，捕获后向前端返回状态码和错误信息
     * @param request
     * @param response
     * @param handler
     * @return true表示流程继续，false表示中断
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("preHandle...");
        User user = (User) request.getSession().getAttribute(MallConst.CURRENT_USER);
        if (user == null) {
            logger.info("user=null");
            throw new UserLoginException();  // 直接抛出一个自定义异常，捕获异常后向前端返回错误信息
        }

        return true;
    }
}
