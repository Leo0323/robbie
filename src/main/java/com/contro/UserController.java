package com.contro;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dto.RegisterDTO;
import com.dto.UsersDto;
import com.mapper.UserMapper;
import com.service.LikeService;
import com.service.UserService;
import com.vo.FileInfo;
import com.vo.Users;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

//@CrossOrigin
@RestController
public class UserController {
    @Resource
     private UserService userService;
    @Resource
      private LikeService likeService;
    @Autowired
    private UserMapper userMapper;

    @GetMapping ("/getUserId/{id}")
     public Users getUser(@PathVariable("id") int id){
        return userService.getUserByIdWithCheck(id);
    }
    @GetMapping  ("/getUserByFileName/{id}")
    public Users getUserByFileName(@PathVariable("id") int id){
        return userMapper.getUserByFileName(id);
    }
    @GetMapping  ("/getUser/all")
    public List< Users>  getAllUser(){
         return userService.list();
    }
    @GetMapping  ("/getUserSex/{sex}")
    public List<  Users> getUserBySex(@PathVariable("sex") String sex){
        return userService.getUserBySex(sex);
    }
    @PostMapping  ("/addUser")
    public String add(@RequestBody Users users){
         return userService.save(users)?"添加成功":"添加失败";
    }
    //分页展示
    @GetMapping  ("/getUserByPage")
    public List< Users> Page(@RequestParam("currentPage") int currentPage){
        return userService.getByPage(currentPage, 5);
    }

    //注册
    @PostMapping  ("/register")
    public String signup(@RequestBody RegisterDTO users){//如果想要后端自动封装，这里必须有@RequestBody
        return userService.signup(users);
    }
    //用户登录判定
    @PostMapping  ("/login")
    public UsersDto login(@RequestBody Users users){
        System.out.println("一登陆");
        return userService.login(users);
    }
    //session+cookie
//    @PostMapping  ("/login")
//    public Users login(@RequestBody Users users, HttpServletRequest request){
//        return userService.login2(users,request);
//    }
    //Github登录
    @GetMapping("/oauth/github/login")
    public void githubLogin1(HttpServletResponse response) throws IOException {
        String clientId = "Ov23li6h0G6c8ohU5SH3";
        String url = "https://github.com/login/oauth/authorize"
                + "?client_id=" + clientId
                + "&scope=user"
                + "&redirect_url=http://localhost:820/robert/oauth/github/callback";
        response.sendRedirect(url);
    }
//github回调
    @GetMapping("/oauth/github/callback")
    //code 是 GitHub 内部生成的，同意授权后，生成的短暂临时标识码
    public void githubCallback(@RequestParam("code") String code,HttpServletResponse  response) throws IOException {
         userService.githubLogin(code,response);
    }

    @DeleteMapping("/LoginOut")
    public String getLoginUser(HttpServletRequest request){
        return userService.loginout(request);
    }


    // 带防重复的点赞接口：http://localhost:820/robert/like/check/1/2
// 含义：userId=2 的用户，给 userId=1 的用户点赞
    @GetMapping("/like/check/{beLikedUserId}/{likeUserId}")
    public String addLikeWithCheck(@PathVariable int beLikedUserId, @PathVariable int likeUserId){
        return likeService.addLike(beLikedUserId, likeUserId);
    }

    // 带校验的取消点赞接口
    @GetMapping("/like/cancel/check/{beLikedUserId}/{likeUserId}")
    public String cancelLikeWithCheck(@PathVariable int beLikedUserId, @PathVariable int likeUserId){
        return likeService.cancelLike(beLikedUserId, likeUserId);
    }

    // 查询某个用户的点赞量和点赞用户
    @GetMapping("/like/count/{userId}")
    public Map<String , Object> getLikeCountAndUsers(@PathVariable int userId){
        return likeService.getLiKeALL(userId);
    }
    //个人空间
    @GetMapping("/UserSpace")
    public Users getUserSpace(HttpServletRequest request){
        return userService.getUser(request);
    }
    @GetMapping("/like/hasLiked/{beLikedUserId}/{likeUserId}")
    public boolean liked(@PathVariable int beLikedUserId, @PathVariable int likeUserId){
        return likeService.liked(beLikedUserId, likeUserId);
    }

    @GetMapping("/like/getUserLike/{userId}")
    public IPage<FileInfo> getUserLike(@PathVariable int userId, int currentPage , int pageSize){
        return likeService.getUserLike(userId, currentPage, pageSize);
    }
    @PostMapping("/updateUser")
    public String updateUser(@RequestBody Users  users,@RequestParam(value = "avatar",required = false) MultipartFile file, HttpServletRequest request)throws  IOException{
        return userService.upadateUser(users,file,request);
    }

    @GetMapping("/getLikeCount/{fileId}")
    public int getLikeCount(@PathVariable("fileId") int beLikedUserId){
        return likeService.getLikeCount(beLikedUserId);
    }

    @PostMapping("/updateBio")
    public String updateBio(MultipartFile  avatar,HttpServletRequest  request) throws IOException {
        return userService.updateBio(avatar, request);
    }

}
