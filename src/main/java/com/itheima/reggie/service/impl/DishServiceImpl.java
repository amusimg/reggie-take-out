package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.DishFlavor;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author
 * @version 1.0.0
 * Create by 2022/6/6 22:47
 * description
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

	@Autowired
	private DishFlavorService dishFlavorService;

	// 新增菜品，同时保存对应的口味数据
	@Transactional
	@Override
	public void saveWithFlavor(DishDto dishDto) {
		this.save(dishDto);

		Long id = dishDto.getId();


		List<DishFlavor> dishFlavorList = dishDto.getFlavors();

		// // 高级写法 菜品口味
		// List<DishFlavor> flavors = dishDto.getFlavors();
		// flavors = flavors.stream().map((item) -> {
		// 	item.setDishId(id);
		// 	return item;
		// }).collect(Collectors.toList());

		for (DishFlavor dishFlavor : dishFlavorList) {
			dishFlavor.setDishId(id);
		}

		dishFlavorService.saveBatch(dishFlavorList);
	}

	// 根据id查询菜品信息和对应的口味信息
	@Override
	public DishDto getByIdWithFlavor(Long id) {

		Dish dish = this.getById(id);

		DishDto dishDto = new DishDto();
		BeanUtils.copyProperties(dish,dishDto);

		LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
		lqw.eq(dish != null,DishFlavor::getDishId,dish.getId());
		List<DishFlavor> dishFlavorList = dishFlavorService.list(lqw);

		dishDto.setFlavors(dishFlavorList);

		return dishDto;
	}

	// 修改菜品信息和对应的口味信息
	@Transactional
	@Override
	public boolean updateWithFlavor(DishDto dishDto) {

		// 更新dish表基本信息
		this.updateById(dishDto);

	    // 清理当前菜品对应口味数据---dish_flavor表的delete操作
		LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
		lqw.eq(dishDto.getId() != null,DishFlavor::getDishId, dishDto.getId());

		boolean flag = dishFlavorService.remove(lqw);

		List<DishFlavor> dishFlavorList = dishDto.getFlavors();

		for (DishFlavor dishFlavor : dishFlavorList) {
			dishFlavor.setDishId(dishDto.getId());
		}

		if (flag) dishFlavorService.saveBatch(dishFlavorList);

		return flag;
		// 添加当前提交过来的口味数据---dish_flavor表的insert操作
	}
}
