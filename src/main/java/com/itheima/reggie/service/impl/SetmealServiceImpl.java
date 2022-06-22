package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.domain.Setmeal;
import com.itheima.reggie.domain.SetmealDish;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author
 * @version 1.0.0
 * Create by 2022/6/6 22:49
 * description
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


	@Autowired
	private SetmealDishService setmealDishService;


	/**
	 * 新增套餐，同时需要保存套餐和菜品的关联关系
 	 */
	@Transactional
	@Override
	public void saveWithDish(SetmealDto setmealDto) {

		// 保存套餐
		this.save(setmealDto);

		Long setmealId = setmealDto.getId();

		List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();

		for (SetmealDish setmealDish : setmealDishList) {
			setmealDish.setSetmealId(setmealId);
		}
		setmealDishService.saveBatch(setmealDishList);
	}

	@Override
	public SetmealDto getByIdWithDish(Long id) {

		Setmeal setmeal = this.getById(id);

		SetmealDto setmealDto = new SetmealDto();
		BeanUtils.copyProperties(setmeal,setmealDto);

		LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
		lqw.eq(setmeal != null,SetmealDish::getSetmealId,setmeal.getId());
		List<SetmealDish> setmealDishList = setmealDishService.list(lqw);
		setmealDto.setSetmealDishes(setmealDishList);
		return setmealDto;
	}

	/**
	 * 修改套餐，同时需要修改套餐和菜品的关联数据
	 */
	@Transactional
	@Override
	public boolean updateWithDish(SetmealDto setmealDto) {

		this.updateById(setmealDto);

		LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper();
		lqw.eq(SetmealDish::getSetmealId,setmealDto.getId());

		boolean flag = setmealDishService.remove(lqw);

		//添加当前提交过来的菜品数据---dish表的insert操作
		List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
		for (SetmealDish setmealDish : setmealDishList) {
			setmealDish.setSetmealId(setmealDto.getId());
		}
		if (flag) setmealDishService.saveBatch(setmealDishList);
		return flag;
	}

	/**
	 * 删除套餐，先停售在删除。同时需要删除套餐和菜品的关联数据
	 */
	@Transactional
	@Override
	public boolean deleteWithDish(List<Long> ids) {

		//查询套餐状态，确定是否可用删除
		LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper();
		lqw.in(Setmeal::getId,ids);
		lqw.eq(Setmeal::getStatus,1);

		int count = this.count(lqw);
		if(count > 0){
			//如果不能删除，抛出一个业务异常
			throw new CustomException("套餐正在售卖中，不能删除");
		}
		//如果可以删除，先删除套餐表中的数据---setmeal
		boolean flag = this.removeByIds(ids);

		//delete from setmeal_dish where setmeal_id in (1,2,3)
		LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
		//删除关系表中的数据----setmeal_dish
		if (flag) setmealDishService.remove(lambdaQueryWrapper);
		return flag;
	}


}
