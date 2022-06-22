package com.itheima.reggie.config;

import com.itheima.reggie.common.JacksonObjectMapper;
import com.itheima.reggie.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/5/17 23:16
 * description
 */
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源配置");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/")
        .addResourceLocations("/resources/");
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        String addPathPatterns[] = {
                "/**"
                // "/employee/**",
                // "/category/**",
        };

        String excludePathPatterns[] = {
                "/backend/**",
                "/front/**",
                "/employee/login",
                "/employee/logout",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
        };
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns(addPathPatterns).excludePathPatterns(excludePathPatterns);
    }

    /**
     * 扩展mvc框架的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // log.info("扩展消息转换器...");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);
    }

    // @Override
    // protected void addViewControllers(ViewControllerRegistry registry) {
    //     registry.addViewController("/").setViewName("forward:static/backend/index.html");
    // }
}
