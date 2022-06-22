package com.itheima.reggie.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/5/18 8:51
 * description
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code; // 1成功， 0和其他皆失败
    private T data;
    private String msg;

    private Map map = new HashMap(); // 动态数据

    public static <T>Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.data = object;
        result.code = 1;
        return result;
    }

    public static <T>Result<T> error(String msg) {
        Result result = new Result();
        result.code = 0;
        result.msg = msg;
        return result;
    }

    public Result<T> add(String key,Object value) {
        this.map.put(key,value);
        return this;
    }

}
