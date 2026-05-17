package com.contro;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mapper.FileMapper;
import com.service.FileService;
import com.vo.FileInfo;
import com.vo.Users;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
//@CrossOrigin
public class FileController {
    @Resource
    FileService fileService;
    @Resource
    FileMapper fileMapper;
    //上传动态
    @ResponseBody
    @PostMapping ("/uploadOne")
    public String upload(@RequestHeader("Authorization") String token, FileInfo fileInfo, @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("describtion") String describtion, @RequestParam("fileName") String name) {
        return fileService.upload(token,fileInfo, name,file, describtion);
    }
    @PostMapping("/uploadOss")
    public String upload1(@RequestHeader("Authorization") String token, FileInfo fileInfo, @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("describtion") String describtion, @RequestParam("fileName") String name) throws IOException {
        return fileService.upload2(token,fileInfo, name,file, describtion);
    }
    //查看所有动态
    @GetMapping ("/getAllFile")
    public List<FileInfo> getAllFile(){
        return fileService.getAllFileByTime();
    }
    //未知
    @GetMapping ("/getFileById/{id}")
    public void getFileById(HttpServletResponse response, @PathVariable("id") int id) throws IOException {
        fileService.ShowById(response,id);
    }
    @GetMapping ("/OssById/{id}")
    public FileInfo OssById(@PathVariable("id") int id) {
        return fileService.OssById(id);
    }

    //更新
    @PutMapping("/updateFile/{id}")
    public String updateById(@PathVariable("id") int id, @RequestBody FileInfo fileInfo, HttpServletRequest  request) {
        return fileService.updateByIds(id,fileInfo,request);
    }
    //用户发布的帖子
    @GetMapping ("/getUserFile/{userId}")
    public IPage<FileInfo> getUserFile(@PathVariable("userId") String id, @RequestHeader("Authorization") String token,int currentPage, @RequestParam int pageSize) {  // 直接当 token 用
        return fileService.getFileByUserName(id, token, currentPage, pageSize);
    }
    //查看最新
    @GetMapping ("/getAllFileByTime")
    public List<FileInfo> getAllFileByTime(){
        return fileService.getAllFileByTime();
    }
    //session
//    @GetMapping ("/getUserFile")
//    public List<FileInfo> getUserFile(HttpSession session) {  // 直接当 token 用
//        return fileService.getFileByUserName2(session);
//    }
    //分页查询文件
//    @GetMapping("/getFileByPage")
//    public Map<String, Object> getFileByPage(@RequestParam int currentPage, @RequestParam int pageSize,@RequestParam String sort) {
//        return fileService.getFileByTimeOrLike(currentPage, pageSize, sort);
//    }
    @GetMapping("/getFileByTime")
    public IPage<FileInfo> getFileByPage(@RequestParam int currentPage, @RequestParam(value = "name",required = false) String name,@RequestParam(value = "type",required = false) String type) {
        return fileService.getFileByTime(currentPage, 10, name, type);
    }
    @GetMapping("/getFileByLike")
    public IPage<FileInfo> getFileByLike(@RequestParam(value = "name",required = false) String name,@RequestParam(value = "type",required = false) String type,@RequestParam int currentPage) {
        return fileService.getAllFileByLike(name, type, currentPage, 10);
    }
    @GetMapping("/JsonById/{id}")
    public FileInfo JsonById(@PathVariable("id") int id) {
        return fileService.getById(id);
    }
    @DeleteMapping ("/deleteFile/{id}")
    public boolean deleteFile(@PathVariable("id") int id) {
        return fileService.removeById(id);
    }
    @DeleteMapping ("/deleteFileBy2/{id}")
    public String deleteFileBy2(@PathVariable("id") int id,HttpServletRequest  request) {
        return fileService.deleteByIds(id, request);
    }
    @GetMapping("/debug/env")
    public Map<String, String> debugEnv() {
        Map<String, String> map = new HashMap<>();
        map.put("ALIYUN_ACCESS_KEY_ID", System.getenv("ALIYUN_ACCESS_KEY_ID"));
        map.put("ALIYUN_ACCESS_KEY_SECRET", System.getenv("ALIYUN_ACCESS_KEY_SECRET"));
        for (String key : map.keySet())
            System.out.println(key + ": " + map.get(key));
        return map;
    }

}
