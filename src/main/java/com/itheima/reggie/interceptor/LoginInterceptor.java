package com.itheima.reggie.interceptor;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.Result;
import com.itheima.reggie.domain.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/5/18 17:45
 * description
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("LoginInterceptor---拦截到请求: {}",request.getRequestURI());
        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);
        System.out.println("session---"+request.getSession().getAttribute("employee"));


        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            return true;
        }

        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            return true;
        }

        log.info("用户未登录");
        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        return false;
    }
}
