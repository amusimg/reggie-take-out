package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.domain.Orders;

public interface OrderService extends IService<Orders> {

    // 用户下单
    boolean submitWithDetail(Orders orders);
}
