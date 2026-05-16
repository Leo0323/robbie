package com.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.RegisterDTO;
import com.dto.UsersDto;
import com.vo.Users;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

//
public interface UserService extends IService<Users> {
    Users getUserByIdWithCheck(int id);
    List<Users >getUserBySex(String  sex);
    List<Users> getByPage(int currentPage, int pageSize);
    UsersDto login(Users users);
    String signup(RegisterDTO users);
    void githubLogin(String code, HttpServletResponse  response) throws IOException;
//    Users login2(Users users, HttpServletRequest  request);
    Users getLoginUser(String token);
    String loginout(HttpServletRequest request);
    Users getUser(HttpServletRequest re);
    String upadateUser(Users users,MultipartFile file, HttpServletRequest request) throws IOException;
    String updateBio(MultipartFile file, HttpServletRequest request) throws IOException;
}
