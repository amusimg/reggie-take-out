package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.Result;
import com.itheima.reggie.domain.Category;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.DishFlavor;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/6/7 22:30
 * description 菜品管理
 */
@RestController
@RequestMapping("dish")
@Slf4j
public class DishController {

	@Autowired
	private DishService dishService;

	@Autowired
	private DishFlavorService dishFlavorService;


	@Autowired
	private CategoryService categoryService;

	@Autowired
	private RedisTemplate redisTemplate;

	@GetMapping("/page")
	public Result<Page> page(int page,int pageSize,String name) {

		Page<Dish> dishPage = new Page<Dish>(page, pageSize);
		Page<DishDto> dishDtoPage = new Page<DishDto>();

		LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
		lqw.like(StringUtils.isNotEmpty(name),Dish::getName,name);
		lqw.orderByDesc(Dish::getUpdateTime);

		//执行分页查询
		dishService.page(dishPage, lqw);

		// 对象拷贝
		BeanUtils.copyProperties(dishPage,dishDtoPage,"records"); // 数据要处理后才返回

		List<Dish> dishList = dishPage.getRecords();

		// 高级写法
		// List<DishDto> list = records.stream().map((item) -> {
		// 	DishDto dishDto = new DishDto();
		//
		// 	BeanUtils.copyProperties(item,dishDto);
		//
		// 	Long categoryId = item.getCategoryId();//分类id
		// 	//根据id查询分类对象
		// 	Category category = categoryService.getById(categoryId);
		//
		// 	if(category != null){
		// 		String categoryName = category.getName();
		// 		dishDto.setCategoryName(categoryName);
		// 	}
		// 	return dishDto;
		// }).collect(Collectors.toList());

		List<DishDto> dishDtoList = new ArrayList<DishDto>();

		for (Dish dish : dishList) {
			Long categoryId = dish.getCategoryId();
			Category category = categoryService.getById(categoryId);

			if (category != null) {
				DishDto dishDto = new DishDto();
				BeanUtils.copyProperties(dish,dishDto);
				String categoryName = category.getName();
				dishDto.setCategoryName(categoryName);
				dishDtoList.add(dishDto);
			}
		}
		dishDtoPage.setRecords(dishDtoList);

		if (dishDtoList != null) {
			return Result.success(dishDtoPage);
		}
		return Result.error("菜品分页查询失败！");
	}

	@PostMapping
	public Result<String> save(@RequestBody DishDto dishDto) {
		log.info("菜品 数据传输对象封装的数据：{}",dishDto);
		String key = "dish_" + dishDto.getCategoryId();
		redisTemplate.delete(key);
		dishService.saveWithFlavor(dishDto);
		return Result.success("新增菜品成功");
	}

	// 根据id查询菜品信息和对应的口味信息
	@GetMapping("/{id}")
	public Result<DishDto> getById(@PathVariable Long id) {
		DishDto dishDto = dishService.getByIdWithFlavor(id);
		return Result.success(dishDto);
	}

	// 宫保鸡丁点蛋
	@PutMapping
	public Result<String> update(@RequestBody DishDto dishDto) {
		log.info("菜品 数据传输对象封装的数据：{}",dishDto);

		// 清理redis全部缓存
		Set keys = redisTemplate.keys("dish_*");
		redisTemplate.delete(keys);
		// String key = "dish_" + dishDto.getCategoryId();
		// redisTemplate.delete(key);
		boolean success = dishService.updateWithFlavor(dishDto);
		if (success) return Result.success("修改菜品成功！");

		return Result.error("修改菜品失败！");
	}

	// @GetMapping("/list")
	// public Result<List<Dish>> listResult(Dish dish) {
	//
	// 	LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
	// 	// 查询启售的菜品
	// 	lqw.eq(Dish::getStatus,1);
	// 	lqw.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
	// 	lqw.orderByAsc(Dish::getSort);
	//
	// 	List<Dish> dishList = dishService.list(lqw);
	//
	// 	if (dishList != null) {
	// 		return Result.success(dishList);
	// 	}
	//
	// 	return Result.error("菜品列表为空");
	// }

	// 改进list
	@GetMapping("/list")
	public Result<List<DishDto>> listResult(Dish dish) {
		List<DishDto> dishDtoList = null;

		// 清理redis缓存
		String key = "dish_" + dish.getCategoryId();
		dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

		if (dishDtoList != null) {
			return Result.success(dishDtoList);
		}

		LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
		//  //添加条件，查询状态为1（起售状态）的菜品
		lqw.eq(Dish::getStatus,1);
		lqw.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
		lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

		List<Dish> dishList = dishService.list(lqw);

		dishDtoList = new ArrayList<DishDto>();
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

		if (dishDtoList != null) {
			redisTemplate.opsForValue().set(key,dishDtoList,30, TimeUnit.MINUTES);
			return Result.success(dishDtoList);
		}
		return Result.error("菜品列表为空");
	}

	@DeleteMapping
	public Result<String> delete(@RequestParam List<Long> ids) {

		boolean flag = dishService.removeByIds(ids);

		return Result.success(flag ? "删除成功" : "删除失败");
	}

	@PostMapping("/status/{state}")
	public Result<String> status(@PathVariable Integer state,@RequestParam List<Long> ids) {
		log.info("状态：{},唯一标识：{}",state,ids);

		List<Dish> dishArrayList = new ArrayList<>();
		// Dish dish = null;
		for (Long id : ids) {
			// 清理redis缓存
			Dish dish1 = dishService.getById(id);
			String key = "dish_" + dish1.getCategoryId();
			redisTemplate.delete(key);

			Dish dish = new Dish();
			dish.setId(id);
			dish.setStatus(state);
			dishArrayList.add(dish);
		}

		boolean flag = dishService.updateBatchById(dishArrayList);
		if (flag) return Result.success("删除成功");

		return Result.error("删除失败");
	}
}
