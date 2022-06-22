package com.itheima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/6/4 0:28
 * description
 */
@Configuration
public class MyBaitsPlusConfig {

	/**
	 * 配置 MP 分页插件
	 * */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor mpi = new MybatisPlusInterceptor();
		mpi.addInnerInterceptor(new PaginationInnerInterceptor());
		return mpi;
	}
}
