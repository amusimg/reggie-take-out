package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.dto.DishDto;

public interface DishService extends IService<Dish> {

	//新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
	void saveWithFlavor(DishDto dishDto);

	DishDto getByIdWithFlavor(Long id);

	boolean updateWithFlavor(DishDto dishDto);
}
