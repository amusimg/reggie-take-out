package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.domain.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author
 * @version 1.0.0
 * Create by 2022/5/18 8:59
 * description
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{

 /*   @Autowired
    private EmployeeMapper userMapper;


    @Override
    public Result<Employee> login(String username, String password) {

        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();

        lqw.eq(Employee::getUsername,username);

        Employee emp = userMapper.selectOne(lqw);

        if (emp == null) {
            return Result.error("用户不存在");
        }else if (!password.equals(emp.getPassword())) {
            return Result.error("密码错误");
        }else if (emp.getStatus() == 0) {
            return Result.error("账号已禁用");
        }
        return Result.success(emp);
    }*/
}
