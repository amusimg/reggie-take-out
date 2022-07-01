package com.itheima.reggie;

import com.itheima.reggie.controller.CommonController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
class ReggieTakeOutApplicationTests {

	@Test
	void contextLoads() {
		ArrayList<Object> list = new ArrayList<>(16);
		System.out.println(list.size());
		byte[] bytes = "kkds".getBytes();
		System.out.println(bytes);
	}

	@Test
	public void testPath() {
		String path2 = CommonController.class.getResource("classpath:").getPath();
		System.out.println(path2);
	}
}
