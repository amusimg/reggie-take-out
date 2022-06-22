package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.domain.UserMail;
import com.itheima.reggie.mapper.UserMailMapper;
import com.itheima.reggie.service.UserMailService;
import org.springframework.stereotype.Service;

/**
 * @author
 * @version 1.0.0
 * Create by 2022/6/12 23:10
 * description
 */
@Service
public class UserMailServiceImpl extends ServiceImpl<UserMailMapper, UserMail> implements UserMailService {
}
