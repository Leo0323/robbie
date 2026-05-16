package com.config;

import com.handler.AuthInterceptor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private AuthInterceptor authInterceptor;
    //注册拦截器。如果不注册，拦截器将不起作用，authInterceptor也永远不会被走过，所以LoginUser也永远存不进去
//拦截器统一写法，于cookie或token无关
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",                        // 已有
                        "/oauth/**",                     // 放过所有 oauth 相关
                        "/oauth/github/callback",        // 精确放过 callback
                        "/oauth/github/login" ,         // 顺便放过登录入口（可选）
                        "/register",
                        "/LoginOut"
                );
    }
}
