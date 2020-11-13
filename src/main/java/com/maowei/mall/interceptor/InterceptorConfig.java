package com.maowei.mall.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        registry.addInterceptor(new UserLoginInterceptor())
                .addPathPatterns("/**") // 默认拦截所有url
                .excludePathPatterns("/user/register", "/user/login", "/categories", "/products/*");
        // 配置拦截器，需要拦截的url放在addPathPatterns中（由于这里需要拦截大多数url，所以
        // 设置成拦截所有url），不需要拦截的url放在excludePathPatterns中。
        // 如果不需要拦截的url太多了，可以单独写一个配置类，通过配置文件注入url
        // 在这个类中声明一个类Autowired进来，从类里把所有url取出来
    }
}
