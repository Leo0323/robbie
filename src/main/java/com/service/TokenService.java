//package com.service;
//
//import com.vo.Users;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class TokenService {
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    public Users getLoginUser(String token) {
//        if (token == null || token.isBlank()) {
//            return null;
//        }
//        return (Users) redisTemplate.opsForValue()
//                .get("login:token:" + token);
//    }
//}
