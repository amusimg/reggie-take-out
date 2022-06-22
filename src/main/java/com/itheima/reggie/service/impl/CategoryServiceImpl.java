package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.domain.Category;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author
 * @version 1.0.0
 * Create by 2022/6/6 21:47
 * description
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

	@Autowired
	private DishService dishService;

	@Autowired
	private SetmealService setmealService;


	/**
	 * 根据id删除分类，删除之前需要进行判断,查询当前分类是否关联了菜品
	 */
	@Override
	public boolean remove(Long id) {
		LambdaQueryWrapper<Dish> lwq = new LambdaQueryWrapper<>();
		lwq.eq(Dish::getCategoryId,id);
		int count = dishService.count(lwq);

		if (count > 0) {
			// 已经关联菜品，抛出一个业务异常
			throw new CustomException("当前分类下已经关联菜品，无法删除！");
		}

		LambdaQueryWrapper<Setmeal> lwq2 = new LambdaQueryWrapper<>();
		lwq2.eq(Setmeal::getCategoryId,id);

		int count2 = setmealService.count(lwq2);

		if (count2 > 0) {
			// 已经关联套餐，抛出一个业务异常
			throw new CustomException("当前分类下已经关联套餐，无法删除！");
		}
		boolean flag = super.removeById(id);
		return flag;
	}
}
