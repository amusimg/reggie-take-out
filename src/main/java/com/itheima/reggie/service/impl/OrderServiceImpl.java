package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.domain.*;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author
 * @version 1.0.0
 * Create by 2022/6/20 22:16
 * description
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper,Orders> implements OrderService {

	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private UserService userService;

	@Autowired
	private AddressBookService addressBookService;

	@Autowired
	private OrderDetailService orderDetailService;

	@Transactional
	@Override
	public boolean submitWithDetail(Orders orders) {
		Long currentId = BaseContext.getCurrentId();

		if (currentId != null) {
			User user = userService.getById(currentId);
			orders.setUserId(currentId);
			orders.setUserName(user.getName());
		}

		AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
		if (addressBook == null) {
			throw new CustomException("用户地址信息有误，不能下单");
		}

		long orderId = IdWorker.getId();
		AtomicInteger amount = new AtomicInteger(0);

		LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
		lqw.eq(currentId != null,ShoppingCart::getUserId,currentId);

		List<ShoppingCart> shoppingCartList = shoppingCartService.list(lqw);

		if(shoppingCartList == null || shoppingCartList.size() == 0){
			throw new CustomException("购物车为空，不能下单");
		}
		Integer number = 0;
		List<OrderDetail> orderDetailList = new ArrayList<>();
		for (ShoppingCart shoppingCart : shoppingCartList) {
			OrderDetail od = new OrderDetail();
			od.setOrderId(orderId);
			od.setName(shoppingCart.getName());
			od.setImage(shoppingCart.getImage());
			od.setDishId(shoppingCart.getDishId());
			od.setSetmealId(shoppingCart.getSetmealId());
			od.setDishFlavor(shoppingCart.getDishFlavor());
			od.setNumber(shoppingCart.getNumber());
			number += shoppingCart.getNumber();
			BigDecimal bigDecimal = shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber()));
			amount.addAndGet(bigDecimal.intValue());
			od.setAmount(bigDecimal);
			orderDetailList.add(od);
		}

		boolean flag = orderDetailService.saveBatch(orderDetailList);

		orders.setId(orderId);
		orders.setOrderTime(LocalDateTime.now());
		orders.setCheckoutTime(LocalDateTime.now());
		orders.setStatus(2);
		orders.setAmount(new BigDecimal(amount.get()));//总金额
		orders.setNumber(number);
		orders.setConsignee(addressBook.getConsignee());
		orders.setPhone(addressBook.getPhone());
		orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
				+ (addressBook.getCityName() == null ? "" : addressBook.getCityName())
				+ (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
				+ (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

		//向订单表插入数据，一条数据
		 flag = this.save(orders);

		// 清空购物车菜品
		shoppingCartService.remove(lqw);
		return flag;
	}
}
