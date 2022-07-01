package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.Result;
import com.itheima.reggie.domain.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/5/18 8:48
 * description
 */
@Slf4j
@RestController
@RequestMapping("employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request,@RequestBody Employee employee) {

        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return Result.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return Result.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return Result.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return Result.success(emp);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        System.out.println("进入退出登录操作！");
        request.getSession().removeAttribute("employee");
        return Result.success("已退出登录");
    }

    @PostMapping
    public Result<String> addEmployee(HttpServletRequest req,@RequestBody Employee emp) {
        System.out.println("进入到添加员工" + emp + LocalDateTime.now() + "-s--s-s-s-" + BaseContext.getCurrentId());

        Long id = (Long) req.getSession().getAttribute("employee");
        if (id != 1) {
            return Result.error("新增员工失败，没有管理员权限");
        }

        log.info(emp.toString());
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        emp.setPassword(password);
        emp.setStatus(1);

        // emp.setCreateTime(LocalDateTime.now());
        // emp.setUpdateTime(LocalDateTime.now());
        // Long empId = (Long) req.getSession().getAttribute("employee");
        // emp.setCreateUser(empId);
        // emp.setUpdateUser(empId);

         employeeService.save(emp);
         return Result.success("新增员工成功");

    }


    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> pageResult(int page,int pageSize,String name) {
        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<Employee>();
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        lqw.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo,lqw);
        return Result.success(pageInfo);
    }


    /**
     * 根据ID修改员工信息
     */
    @PutMapping()
    public Result<String> updateEmp(HttpServletRequest req, @RequestBody Employee emp) {
        log.info(emp.toString());

        // long id = Thread.currentThread().getId();
        // log.info("线程id为：{}",id);

        Long empId = (Long) req.getSession().getAttribute("employee");
        if (empId != 1) {
            return Result.error("修改失败，没有管理员权限！");
        }

        // Long empId = (Long) req.getSession().getAttribute("employee");
        // emp.setUpdateTime(LocalDateTime.now());
        // emp.setUpdateUser(empId);
        employeeService.updateById(emp);
        return Result.success("员工信息修改成功！");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息...");

        // if (BaseContext.getCurrentId() != 1) {
        //     return Result.error("没有查询到对应员工信息");
        // }
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return Result.success(employee);
        }
        return Result.error("没有查询到对应员工信息");
    }
}
