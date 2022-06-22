package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/6/3 23:35
 * description 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
		log.error(ex.getMessage());
		if (ex.getMessage().contains("Duplicate entry")) {
			String[] split = ex.getMessage().split(" ");
			String str = split[2] + "已存在";
			return Result.error(str);
		}
		return Result.error("未知的错误！");
	}


	@ExceptionHandler(CustomException.class)
	public Result<String> customExceptionHandler(CustomException ex) {
		log.error(ex.getMessage());
		return Result.error(ex.getMessage());
	}
}
