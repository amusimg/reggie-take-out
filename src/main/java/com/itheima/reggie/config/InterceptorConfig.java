package com.itheima.reggie.config;

import com.itheima.reggie.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/5/18 17:53
 * description
 */
// @Slf4j
// @Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    // private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        String addPathPatterns[] = {
                // "/backend/page/**",
                // "/front/page/**",
                "/employee/**",
        };

        String excludePathPatterns[] = {
                "/static/backend/page/**",
                "/static/front/page/**",
                "/employee/login",
                "/employee/logout"
        };

        registry.addInterceptor(new LoginInterceptor()).addPathPatterns(addPathPatterns).excludePathPatterns(excludePathPatterns);
        WebMvcConfigurer.super.addInterceptors(registry);
    }


    // 静态资源映射
    // @Override
    // public void addResourceHandlers(ResourceHandlerRegistry registry) {
    //     log.info("拦截addResourceHandlers");
    //     registry.addResourceHandler("/**").addResourceLocations("classpath:static/**");
    //     WebMvcConfigurer.super.addResourceHandlers(registry);
    // }

    // @Override
    // public void addViewControllers(ViewControllerRegistry registry) {
    //     registry.addViewController("/").setViewName("redirect:/backend/index.html");
    //     WebMvcConfigurer.super.addViewControllers(registry);
    // }
}
