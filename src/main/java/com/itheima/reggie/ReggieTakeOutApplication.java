package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement //开启事务支持/启用事务管理
// @ServletComponentScan(basePackages = "com.itheima.reggie.Filter")  // Servlet 组件扫描
public class ReggieTakeOutApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReggieTakeOutApplication.class, args);
		log.info("spring boot project is success running...");
	}

}
