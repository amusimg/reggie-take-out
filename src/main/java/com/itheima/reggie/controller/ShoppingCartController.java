package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalExceptionHandler;
import com.itheima.reggie.common.Result;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.ShoppingCart;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/6/17 1:22
 * description
 */
@RestController
@RequestMapping("shoppingCart")
@Slf4j
public class ShoppingCartController {


	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private DishService dishService;


	@PostMapping("/add")
	public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
		log.info("添加购物车{}",shoppingCart);

		//设置用户id，指定当前是哪个用户的购物车数据
		Long currentId = BaseContext.getCurrentId();
		shoppingCart.setUserId(currentId);

		Long dishId = shoppingCart.getDishId();

		LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
		lqw.eq(currentId != null,ShoppingCart::getUserId,currentId);
		if (dishId != null) {
			//添加到购物车的是菜品
			lqw.eq(ShoppingCart::getDishId,dishId);
		}else{
			//添加到购物车的是套餐
			lqw.eq(shoppingCart.getSetmealId() != null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
		}
		//查询当前菜品或者套餐是否在购物车中
		ShoppingCart cart = shoppingCartService.getOne(lqw);

		if (cart != null) {
			// 购物车数量 +1
			Integer number = cart.getNumber();
			cart.setNumber(number + 1);
			shoppingCartService.updateById(cart);
		}else {
			//如果不存在，则添加到购物车，数量默认就是一
			shoppingCart.setNumber(1);
			shoppingCart.setCreateTime(LocalDateTime.now());
			shoppingCartService.save(shoppingCart);
			cart = shoppingCart;
		}
		// return Result.success("已加入购物车");
		return Result.success(cart);
	}

	@PostMapping("/sub")
	public Result<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {

		//设置用户id，指定当前是哪个用户的购物车数据
		Long currentId = BaseContext.getCurrentId();
		LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
		lqw.eq(currentId != null,ShoppingCart::getUserId,currentId);
		if (shoppingCart.getDishId() != null) {
			lqw.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
		}else {
			lqw.eq(shoppingCart.getSetmealId() != null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
		}
		ShoppingCart cartServiceOne = shoppingCartService.getOne(lqw);

		if (cartServiceOne != null) {
			Integer number = cartServiceOne.getNumber();
			if (number > 1) {
				cartServiceOne.setNumber(number - 1);
				shoppingCartService.updateById(cartServiceOne);
				return Result.success(cartServiceOne);
			}
			shoppingCartService.removeById(cartServiceOne);
			return Result.success(cartServiceOne);
		}
		return Result.error("购物车为空");
	}


	@GetMapping("/list")
	public Result<List<ShoppingCart>> listResult() {
		Long currentId = BaseContext.getCurrentId();

		LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
		lqw.eq(currentId != null,ShoppingCart::getUserId,currentId);

		List<ShoppingCart> shoppingCartList = shoppingCartService.list(lqw);

		return Result.success(shoppingCartList);
	}

	@DeleteMapping("/clean")
	public Result<String> clean() {
		Long currentId = BaseContext.getCurrentId();

		LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
		lqw.eq(currentId != null,ShoppingCart::getUserId,currentId);
		shoppingCartService.remove(lqw);

		return Result.success("购物车已清空");
	}
}
