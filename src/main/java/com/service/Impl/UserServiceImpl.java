package com.service.Impl;


import com.Util.UploadUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dto.RegisterDTO;
import com.dto.UsersDto;
import com.mapper.UserMapper;
import com.service.UserService;
import com.vo.Users;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, Users> implements UserService {
    private final RedisTemplate redisTemplate;
    @Resource
    private UserMapper userMapper;
    public UserServiceImpl(@Qualifier("redisTemplate") RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Users getUserByIdWithCheck(int userId) {
        Users user = this.getById(userId);
            return user;
    }
    //QueryWrapper作用就是生成sql语句，然后作为参数放到原本的IService提供的方法中，所有添加和补充还是在service的实现层完成，controller只写三行
    public List<Users> getUserBySex(String  sex){
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sex",sex);
        List< Users> users =this.list(queryWrapper);
        return users; //或者直接return this.list(queryWrapper);少写一行声明
    }

    //分页查询
    //返回值是IPage<User>而不是List时，会返回总条数和总页数及当前页，更方便前端开发，总体上没什么不同
    public List<Users> getByPage(int currentPage, int pageSize){
        IPage<Users> page = new Page<>(currentPage, pageSize);
        return this.page(page).getRecords();
    }

    //注册
    @Override
    public String signup(RegisterDTO users){
        String username = users.getUsername();
        String password = users.getPassword();
        String confirmPwd = users.getConfirmPassword();
        String email = users.getEmail();
        String sex = users.getSex();
        if (userMapper.countByEmail(email) > 0){
            return "邮箱已存在";
        }
        if (!password.equals(confirmPwd)){
            return "密码不一致";
        }
        Users users1 = new Users(username,password,email,sex,null);
        this.save(users1);
        return "注册成功";
    }
    //登录
    public UsersDto login(Users users) {
        String userName = users.getUserName();
        String password = users.getPassword();
        String token = UUID.randomUUID().toString();
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.allEq(Map.of("username", userName, "password", password));
        if (this.list(queryWrapper).size() > 0) {
            Users users1 =this.list(queryWrapper).get(0);
            users1.getUserName();
            redisTemplate.opsForValue().set(
                    "login:token:" + token,
                    users1,
                    30,
                    TimeUnit.MINUTES
            );
            System.out.println("登录成功"+users1.getUserId());
            return new UsersDto(users1.getUserId(),users1.getUserName(), token);
        }
        return null;
    }

    public String loginout(HttpServletRequest request) {
        Users user = (Users) request.getAttribute("loginUser");
        // 拦截器里存的token就是处理过的
        String auth = request.getHeader("Authorization");
        String token = auth.startsWith("Bearer ") ? auth.substring(7) : auth;
        redisTemplate.delete("login:token:" + token);
        return "注销成功";
    }
    //sesssion+cookie
//    public Users checkLogin(String userName, String password) {
//        QueryWrapper<Users> query = new QueryWrapper<>();
//        query.eq("userName", userName).eq("password", password);
//        return userMapper.selectOne(query);
//    }
//    public Users login2(Users user, HttpServletRequest request) {
//        Users user1 = checkLogin(user.getUserName(), user.getPassword());
//        if (user1 != null) {
//            // 登录成功，放入 session
//            request.getSession().setAttribute("loginUser", user1);
//            return user1;
//        }
//        return null;
//    }
    public void githubLogin(String code, HttpServletResponse  response) throws IOException {
        System.out.println("GitHub回调进入了！！ code=" + code);
        RestTemplate restTemplate = new RestTemplate();//Spring 提供的工具，用来发 HTTP 请求，就像浏览器访问网页一样
        // ========= 1. 用 code 换 access_token ========
        HttpHeaders headers = new HttpHeaders();//HTTP 请求头，告诉 GitHub 你发送的数据类型是什么。
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);//GitHub 要求用 表单格式 发送参数
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));//设置返回格式为 JSON 格式的数据
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();//请求头：MultiValueMap只是存放params的键值对的一种数据结构
        params.add("client_id", "Ov23li6h0G6c8ohU5SH3");
        params.add("client_secret", "5c0a237ea4990180da24851a95321332169e7538");
        params.add("code", code);

        //HttpEntity是把 params（请求体）和 headers（请求头） 打包在一起发送HTTP请求的：body 就是表单数据 params
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);//params是body，headers是请求头
        //MultiValueMap每个 key 可以有多个 value，Map一个 key 对应一个 value
        Map tokenResp = restTemplate.postForObject(//restTemplate用postForObject发 POST 请求给 GitHub，返回 GitHub 返回的 JSON 对象
                "https://github.com/login/oauth/access_token",
                request,
                Map.class
        );
        String accessToken = (String) tokenResp.get("access_token");
        if (accessToken == null) {
            throw new RuntimeException("GitHub 登录失败，access_token为空，返回内容：" + tokenResp);
        }
        // ========= 2. 用 token 获取 GitHub 用户 =========
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.set("Authorization", "Bearer " + accessToken); //Bearer xxx
        //userHeaders.set("Authorization",  accessToken);
        //HTTP 请求有两部分：URL + body
        //GET 请求只在 URL 上带参数（?param=xxx&param2=yyy）
        //POST/PUT 请求可以在 body 里带数据
        HttpEntity<Void> userReq = new HttpEntity<>(userHeaders);
        Map userInfo = restTemplate.exchange(//get请求用exchange
                "https://api.github.com/user",
                HttpMethod.GET,
                userReq,
                Map.class
        ).getBody();
        //id和login是 GitHub API 自己定义的字段名
        String githubId = String.valueOf(userInfo.get("id"));
        String username = (String) userInfo.get("login");
        // ========= 3. 查数据库 =========
        Users user = this.findByGithubId(githubId);
        if (user == null) {
            // 自动注册
            user = new Users();
            user.setGithubId(githubId);
            user.setUserName(username);
            userMapper.insert(user);
        }
        if (user.getUserId() <= 0) {
            throw new RuntimeException("用户 ID 无效，userId=" + user.getUserId());
        }
        String token = UUID.randomUUID().toString();
        user.getUserName();
        redisTemplate.opsForValue().set(
                "login:token:" + token, user, 30, TimeUnit.MINUTES
        );
        // ========= 4. 返回登录用户 =========
        String redirectUrl = "http://127.0.0.1:8848/ondary-fixed-navigation-master/index.html"
                + "?token=" + token
                + "&userId=" + user.getUserId()
                + "&username=" + user.getUserName();
        System.out.println("redirectUrl: "+redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    Users findByGithubId(String githubId){
        QueryWrapper<Users> query = new QueryWrapper<>();
        query.eq("github_id", githubId);
        return this.getOne(query);
    }

    public Users getLoginUser(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String key = "login:token:" + token;
        Users user = (Users) redisTemplate.opsForValue().get(key);//从 Redis 中获取键为 key 的 String 类型的值，并强制转换为 Users 对象。通常用于从缓存中获取用户信息。
        if (user != null) {
            // ⭐ 滑动过期：每次访问成功就续期
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        }
        System.out.println(user.toString()+user.getAvatar());
        return user;
    }


    public Users getUser(HttpServletRequest  request){
        String auth = request.getHeader("Authorization");
        String token;
// 前端如果传的是 Bearer xxx
        if (auth.startsWith("Bearer")) {
            token = auth.substring(7);
        } else {
            // 前端直接传 token
            token = auth;
        }
        return getLoginUser(token);
    }

    // ... existing code ...
    public String upadateUser(Users users, MultipartFile file, HttpServletRequest request) throws  IOException {
        Users user = (Users) request.getAttribute("loginUser");
        if (user.getUserId() != users.getUserId()) {
            return "Illegal";
        }

        // 获取当前token用于后续更新Redis
        String auth = request.getHeader("Authorization");
        String token = auth.startsWith("Bearer ") ? auth.substring(7) : auth;
        String redisKey = "login:token:" + token;

        // 有传文件才更新头像
        if (file != null && !file.isEmpty()) {
            String ossUrl = updateBio(file, request);
            user.setAvatar(ossUrl);  // 用新的ossUrl
        }
        // 其他字段：有值才更新，没值保留原来的
        if (users.getEmail() != null)    user.setEmail(users.getEmail());
        if (users.getSex() != null)      user.setSex(users.getSex());
        if (users.getUserName() != null) user.setUserName(users.getUserName());
        if (users.getPassword() != null) user.setPassword(users.getPassword());
        if (users.getBio() != null)  user.setBio(users.getBio());
        System.out.println( user.toString());
        this.updateById(user);  // 用updateById就够了，不需要UpdateWrapper

        // 更新Redis中的用户数据
        redisTemplate.opsForValue().set(redisKey, user, 30, TimeUnit.MINUTES);

        return "Record updated successfully.";
    }
    public String updateBio(MultipartFile file, HttpServletRequest request) throws IOException {
        // 2. 检查用户
        Users user = (Users) request.getAttribute("loginUser");

        // 获取当前token用于后续更新Redis
        String auth = request.getHeader("Authorization");
        String token = auth.startsWith("Bearer ") ? auth.substring(7) : auth;
        String redisKey = "login:token:" + token;

        String uuid = UUID.randomUUID().toString();
        String suffix = ".png";
        String finalFileName = uuid + suffix;
        String ossUrl = UploadUtil.uploadFile(file, uuid);
        user.setAvatar(ossUrl);
        boolean updateResult = this.updateById(user);
        System.out.println("更新结果=" + updateResult + " ossUrl=" + ossUrl + " userId=" + user.getUserId());
        // 更新Redis中的用户数据
        redisTemplate.opsForValue().set(redisKey, user, 30, TimeUnit.MINUTES);

        return ossUrl;
    }
// ... existing code ...

// ... existing code ...


}
