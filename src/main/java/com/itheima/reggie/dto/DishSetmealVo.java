package com.itheima.reggie.dto;

import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.DishFlavor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishSetmealVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<DishDto> dishDtoList;

    private List<SetmealDto> setmealDtoList;

}
