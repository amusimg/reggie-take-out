package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.Result;
import com.itheima.reggie.domain.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/6/6 21:49
 * description
 */
@RestController
@RequestMapping("category")
@Slf4j
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@PostMapping
	public Result<String> addCategory(@RequestBody Category cg) {

		boolean flag = categoryService.save(cg);

		log.info("进入到添加分类: {},{}",flag,cg);
		if (flag) {
			return Result.success("新增分类成功！");
		}
		return Result.error("新增分类失败！");
	}


	// 分类分页查询
	@GetMapping("/page")
	public Result<Page> page(int page, int pageSize) {

		Page<Category> categoryPage = new Page(page,pageSize);

		LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
		lqw.orderByAsc(Category::getSort);

		Page<Category> pageInfo = categoryService.page(categoryPage,lqw);

		if (pageInfo != null) {
			return Result.success(pageInfo);
		}
		return Result.error("分页查询失败！");
	}

	// 删除分类
	@DeleteMapping
	public Result<String> delete(@RequestParam("ids") Long id) {
		log.info("删除分类的ID：{}",id);
		/*boolean flag = categoryService.removeById(ids);
		if (flag) {
			return Result.success("删除分类成功！");
		}
		return Result.error("删除分类失败！");*/
		boolean flag = categoryService.remove(id);

		if (flag) {
			return Result.success("删除分类成功！");
		}
		return Result.error("删除分类失败！");
	}

	/**
	 * 根据id修改分类信息
	 */
	@PutMapping
	public Result<String> update(@RequestBody Category category){
		log.info("修改分类信息：{}",category);

		categoryService.updateById(category);

		return Result.success("修改分类信息成功");
	}

	@GetMapping("/list")
	public Result<List<Category>> getList(Category cg) {

		LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
		lqw.eq(cg.getType() != null,Category::getType,cg.getType());
		lqw.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
		List<Category> categoryList = categoryService.list(lqw);

		if (categoryList.size() > 0) {
			return Result.success(categoryList);
		}


		return Result.error("分类列表为空数据");
	}
}
