package com.handler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// 核心注解1：全局异常处理器，处理所有@RestController的异常
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 核心注解2：捕获所有的RuntimeException异常（你抛出的就是这个）
    @ExceptionHandler(RuntimeException.class)
    public Map<String, Object> handleRuntimeException(RuntimeException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500); // 业务失败状态码
        result.put("msg", e.getMessage()); // 你的自定义提示文字（用户不存在/admin）
        result.put("data", null);
        return result;
    }
}