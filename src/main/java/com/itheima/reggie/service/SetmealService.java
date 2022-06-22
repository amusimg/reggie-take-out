package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.Setmeal;
import com.itheima.reggie.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
	void saveWithDish(SetmealDto setmealDto);

	boolean updateWithDish(SetmealDto setmealDto);

	boolean deleteWithDish(List<Long> ids);

	SetmealDto getByIdWithDish(Long id);
}
