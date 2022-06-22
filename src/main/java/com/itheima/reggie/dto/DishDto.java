package com.itheima.reggie.dto;

import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * data transfer object
 * */
@Data
public class DishDto extends Dish {

    // 菜品对应的口味数据
    private List<DishFlavor> flavors; // = new ArrayList<>()

    private String categoryName;

    private Integer copies;
}
