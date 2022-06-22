package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.Result;
import com.itheima.reggie.domain.Category;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.Setmeal;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/6/9 16:49
 * description
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {


	@Autowired
	private SetmealService setmealService;

	@Autowired
	private CategoryService categoryService;

	@PostMapping
	public Result<String> save(@RequestBody SetmealDto setmealDto) {
		log.info("套餐信息：{}",setmealDto);
		setmealService.saveWithDish(setmealDto);

		return Result.success("新增套餐"+setmealDto.getName()+"成功");
	}

	@GetMapping("/page")
	public Result<Page> page(int page,int pageSize,String name) {

		Page<Setmeal> setmealPage = new Page<>(page,pageSize);

		Page<SetmealDto> setmealDtoPage = new Page<>(page,pageSize);

		LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
		lqw.like(StringUtils.isNotEmpty(name), Setmeal::getName,name);
		lqw.orderByDesc(Setmeal::getUpdateTime);

		// 分页查询
		setmealService.page(setmealPage, lqw);

		BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");


		List<Setmeal> records = setmealPage.getRecords();

		List<SetmealDto> setmealDtoList = new ArrayList<SetmealDto>();

		for (Setmeal setmeal : records) {

			Long categoryId = setmeal.getCategoryId();
			Category category = categoryService.getById(categoryId);

			if (category != null) {
				SetmealDto setmealDto = new SetmealDto();
				BeanUtils.copyProperties(setmeal,setmealDto);
				setmealDto.setCategoryName(category.getName());
				setmealDtoList.add(setmealDto);
			}
		}
		setmealDtoPage.setRecords(setmealDtoList);

		return Result.success(setmealDtoPage);
	}


	@GetMapping("{id}")
	public Result<SetmealDto> getById(@PathVariable Long id) {

		SetmealDto setmealDto = setmealService.getByIdWithDish(id);
		return Result.success(setmealDto);
	}

	@PutMapping
	public Result<String> update(@RequestBody SetmealDto setmealDto) {
		log.info("修改套餐{}",setmealDto);
		boolean flag = setmealService.updateWithDish(setmealDto);

		if (flag) {
			return Result.success("修改套餐成功");
		}
		return Result.error("修改套餐失败");

	}


	@DeleteMapping
	public Result<String> delete(@RequestParam List<Long> ids) {
		log.info("删除套餐ID列表{}",ids);
		boolean flag = setmealService.deleteWithDish(ids);
		if (flag) return Result.success("删除套餐成功");
		return Result.error("删除套餐失败");
	}

	@PostMapping("/status/{state}")
	public Result<String> status(@PathVariable Integer state,@RequestParam List<Long> ids) {
		log.info("状态{},启停售ids{}",state,ids);

		List<Setmeal> setmealList = new ArrayList<>();
		for (Long id : ids) {
			Setmeal sm = new Setmeal();
			sm.setStatus(state);
			sm.setId(id);
			setmealList.add(sm);
		}
		boolean flag = setmealService.updateBatchById(setmealList);
		return Result.success(flag ? "状态已更新" : "更新状态失败");
	}

	@GetMapping("/list")
	public Result<List<Setmeal>> listResult(Setmeal setmeal) {

		LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<Setmeal>();
		// 查询启售的菜品
		lqw.eq(Setmeal::getStatus,1);
		lqw.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
		lqw.orderByAsc(Setmeal::getUpdateTime);

		List<Setmeal> setmealList = setmealService.list(lqw);

		if (setmealList != null) {
			return Result.success(setmealList);
		}

		return Result.error("套餐列表为空");
	}
}
