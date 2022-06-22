package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.Result;
import com.itheima.reggie.domain.*;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.DishSetmealVo;
import com.itheima.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/6/20 22:18
 * description
 */
@RestController
@RequestMapping("order")
@Slf4j
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private DishService dishService;

	@Autowired
	private SetmealService setmealService;

	@Autowired
	private OrderDetailService orderDetailService;

	@Autowired
	private DishController dishController;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private DishFlavorService dishFlavorService;

	@PostMapping("/submit")
	public Result<String> submit(@RequestBody Orders orders) {
		log.info("支付订单{}",orders);

		boolean flag = orderService.submitWithDetail(orders);
		if (flag) return Result.success("下单成功");

		return Result.error("支付失败，请重试");
	}

	@GetMapping("/userPage")
	public Result<Page> pageResult(int page,int pageSize) {

		Page ordersPage = new Page(page,pageSize);
		Long currentId = BaseContext.getCurrentId();

		LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<Orders>();
		lqw.eq(currentId != null,Orders::getUserId,currentId);
		lqw.orderByDesc(Orders::getOrderTime).orderByDesc(Orders::getNumber);

		orderService.page(ordersPage,lqw);

		return Result.success(ordersPage);
	}

	@GetMapping("/newOrderList")
	public Result<List<DishDto>> getNewOrderList() {
		List<DishDto> dishDtoList = new ArrayList<DishDto>();
		LambdaQueryWrapper<OrderDetail> lqw = new LambdaQueryWrapper<>();
		lqw.orderByAsc(OrderDetail::getNumber);

		List<OrderDetail> orderDetailList = orderDetailService.list(lqw);
		log.info("orderDetailList-----------{},========={}",orderDetailList.size() ,orderDetailList == null);
		if (orderDetailList.size() < 1) {
			dishDtoList = DishPage(1, 10);
		}else {

			A:for (OrderDetail od : orderDetailList) {
				if (dishDtoList.size() >= 10) {
					break;
				}
				for (DishDto dishDto : dishDtoList) {
					if (od.getDishId() == null) break;
					log.info("相同的菜品?{}",od.getDishId(),dishDto.getId(),od.getDishId().longValue() == dishDto.getId().longValue());
					if (od.getDishId().equals(dishDto.getId()) || od.getDishId().longValue() == dishDto.getId().longValue()) {
						od.setNumber(od.getNumber() + 1);
						continue A;
					}
					// dishDtoList.sort(Comparator.comparing(dishDto::getCopies).reversed());
				}

				DishDto dto = new DishDto();
				Long dishId = od.getDishId();
				if (dishId == null) continue;

				Dish dish = dishService.getById(dishId);
				BeanUtils.copyProperties(dish,dto);
				BeanUtils.copyProperties(od.getNumber(),dto.getCopies());


				Long categoryId = dto.getCategoryId();
				//根据id查询分类对象
				Category category = categoryService.getById(categoryId);
				if (category != null) {
					dto.setCategoryName(category.getName());
				}
				Long dsId = dto.getId();
				LambdaQueryWrapper<DishFlavor> lwq2 = new LambdaQueryWrapper<DishFlavor>();
				lwq2.eq(dsId != null,DishFlavor::getDishId,dsId);
				List<DishFlavor> dishFlavorList = dishFlavorService.list(lwq2);
				dto.setFlavors(dishFlavorList);
				dishDtoList.add(dto);
				log.info("订单中的数据{}",dto);

			}
			log.info("推荐数据增加至{}条",dishDtoList.size());
		}
		if (dishDtoList.size() < 10) {
			List<DishDto> dishPage = DishPage(1, 10 - dishDtoList.size());

			for (DishDto dto : dishPage) {
				if (dishDtoList.size() >= 10) {
					break;
				}
				dishDtoList.add(dto);
				log.info("推荐数据添加的{}",dto);
			}
			log.info("推荐数据增加至{}条{}",dishDtoList.size(),dishDtoList);
		}
		log.info("新款推荐返回数据{}",dishDtoList);
		return Result.success(dishDtoList);
	}

	public List<DishDto> DishPage(int page,int pageSize) {

		Page<Dish> p = new Page<Dish>(page,pageSize);
		LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
		//  //添加条件，查询状态为1（起售状态）的菜品
		lqw.eq(Dish::getStatus,1);
		lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

		dishService.page(p,lqw);

		List<Dish> dishList = p.getRecords();

		List<DishDto> dishDtoList = new ArrayList<DishDto>();

		for (Dish ds : dishList) {
			DishDto dto = new DishDto();
			BeanUtils.copyProperties(ds,dto);
			Long categoryId = ds.getCategoryId();
			//根据id查询分类对象
			Category category = categoryService.getById(categoryId);
			if (category != null) {
				dto.setCategoryName(category.getName());
			}
			Long dsId = ds.getId();
			LambdaQueryWrapper<DishFlavor> lwq2 = new LambdaQueryWrapper<DishFlavor>();
			lwq2.eq(dsId != null,DishFlavor::getDishId,dsId);
			List<DishFlavor> dishFlavorList = dishFlavorService.list(lwq2);
			dto.setFlavors(dishFlavorList);
			dishDtoList.add(dto);
		}
		return dishDtoList;
	}

	// private List<>
}
