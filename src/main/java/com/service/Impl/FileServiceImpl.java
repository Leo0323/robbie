package com.service.Impl;

import com.Util.UploadUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapper.FileMapper;
import com.service.FileService;
import com.vo.FileInfo;
import com.vo.Users;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileInfo> implements FileService {

    private final RedisTemplate redisTemplate;
    @Autowired
    private FileMapper fileMapper;

    public FileServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Resource
    private StringRedisTemplate SredisTemplate;

    @Value("${file.upload-path}")
    private String uploadPath;

//磁盘式存储
    public String upload(String token,FileInfo fileInfo1, String name, MultipartFile file, String describtion) {
        String redisKey = "login:token:" + token;
        Users loginUser = (Users) redisTemplate.opsForValue().get(redisKey);
        if (file == null || file.isEmpty()) {
            fileInfo1.setUserName(loginUser.getUserId());

            fileInfo1.setFileType("document");
            fileInfo1.setFileSuffix(null);
            return this.save(fileInfo1) ? "发布成功（无附件）" : "发布失败";
        }
        String originalFileName = file.getOriginalFilename();
        String suffix = "";
        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            suffix = originalFileName.substring(lastDotIndex);
        }
        switch (suffix) {
            case ".png":
            case ".jpg":
            case ".gif":
            case ".jpeg":
                fileInfo1.setFileType("image");
                break;
            case ".mp4":
            case ".avi":
                fileInfo1.setFileType("video");
                break;
            case ".mp3":
            case ".m4a":
                fileInfo1.setFileType("audio");
                break;
        }

        LocalDateTime now = LocalDateTime.now();
        fileInfo1.setUploadTime(now);
        fileInfo1.setUserName(loginUser.getUserId());
        fileInfo1.setFileName(name);
        fileInfo1.setFileSuffix(suffix);
        fileInfo1.setDescribtion(describtion);
        if (originalFileName == null || originalFileName.isEmpty()) {
            return "文件名称无效！";
        }
        String userDir = uploadPath + "/"+loginUser.getUserName() + "/";

        File dir = new File(userDir);
        if (!dir.exists()) {
            dir.mkdirs(); // 自动创建目录
        }

        // 防止重名（推荐）
        String finalFileName = name + suffix;

        File targetFile = new File(userDir + finalFileName);

        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            return "文件上传失败：" + e.getMessage();
        }
        return this.save(fileInfo1) ? "上传成功" : "上传失败";
    }

    public String upload2(String token,FileInfo fileInfo1, String name, MultipartFile file, String describtion) throws IOException {
        System.out.println("2-implement upload function");
        String redisKey = "login:token:" + token;
        Users loginUser = (Users) redisTemplate.opsForValue().get(redisKey);
        if (file == null || file.isEmpty()) {
            fileInfo1.setUserName(loginUser.getUserId());
            fileInfo1.setFileType("document");
            fileInfo1.setFileSuffix(null);
            return this.save(fileInfo1) ? "发布成功（无附件）" : "发布失败";
        }
        String originalFileName = file.getOriginalFilename();
        String suffix ="";
        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            suffix = originalFileName.substring(lastDotIndex);
        }
        String type = this.suffix_judge(suffix);
//        switch (suffix) {
//            case ".png":
//            case ".jpg":
//            case ".gif":
//            case ".jpeg":
//                fileInfo1.setFileType("image");
//                break;
//            case ".mp4":
//            case ".avi":
//                fileInfo1.setFileType("video");
//                break;
//            case ".mp3":
//            case ".m4a":
//                fileInfo1.setFileType("audio");
//                break;
//        }
        LocalDateTime now = LocalDateTime.now();
        fileInfo1.setUploadTime(now);
        fileInfo1.setUserName(loginUser.getUserId());
        fileInfo1.setFileName(name);
        fileInfo1.setFileSuffix(suffix);
        fileInfo1.setFileType(type);
        fileInfo1.setDescribtion(describtion);
        if (originalFileName == null || originalFileName.isEmpty()) {
            return "文件名称无效！";
        }
        UploadUtil uploadUtil = new UploadUtil();
        String path = uploadUtil.uploadFile(file, name);
        fileInfo1.setOssUrl(path);
        this.save(fileInfo1);
        return path;
    }
    public String suffix_judge(String suffix){
        switch (suffix) {
            case ".png":
            case ".jpg":
            case ".gif":
            case ".jpeg":
                return "image";
            case ".mp4":
            case ".avi":
                return "video";
            case ".mp3":
            case ".m4a":
                return "audio";
        }
        return "document";
    }

    public IPage<FileInfo> getFileByUserName(String name, String token, int currentPage, int pageSize) {
        IPage<FileInfo> page = new Page<>(currentPage, pageSize);
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", name);
        String redisKey = "login:token:" + token;
        Users loginUser = (Users) redisTemplate.opsForValue().get(redisKey);

        if (loginUser == null) {
            // token 失效或者不存在
            throw new RuntimeException("未登录或登录已过期，请重新登录");
        }
        return this.page(page,queryWrapper);
    }

    public List<FileInfo> getFileByUserName2(HttpSession session) {
        Users loginUser = (Users) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new RuntimeException("未登录或登录已过期");
        }
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginUser.getUserName());
        return this.list(queryWrapper);
    }



    public List<FileInfo> getAllFileByTime() {
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("upload_time");
        return this.list(queryWrapper);
    }
//    public List<FileInfo> getFileByUserAndType() {
//        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
//        return this.list();
//    }

    public void ShowById(HttpServletResponse response, int id) throws IOException {
        FileInfo file = this.getById(id);
        System.out.println( file.toString());
        String realFile = "D:\\Java\\iseeyou\\iseeyou-副本\\src\\main\\resources\\static\\Percy\\"+file.getUserName()+"\\"+file.getFileName()+file.getFileSuffix();
        System.out.println(realFile);
        System.out.println(realFile);
        // 3. 读取文件并输出二进制流
        File f = new File(realFile);
        System.out.println(realFile);
        // 设置响应类型（图片）
//        response.setContentType("image/png");
        //setContentType作用是告诉客户端“收到的二进制数据该用什么方式解析”
//        response.setContentType("application/octet-stream");
        if (file.getFileType().equals("image")){
            response.setContentType("image/png");
        }else if (file.getFileType().equals("video")){
            response.setContentType("video/mp4");
        }else if (file.getFileType().equals("doc") || file.getFileType().equals("txt")){
//            response.setContentType("application/msword");
            response.setContentType("text/plain");
        }else if (file.getFileType().equals("music")){
            response.setContentType("audio/mp3");
        }else {
            response.setContentType("application/octet-stream");
        }
        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(f);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        in.close();
        out.close();
    }

    public FileInfo OssById(int id) {
            FileInfo file = this.getById(id);
            String getSignedUrl = UploadUtil.getSignedUrl(file.getOssUrl());
            return new FileInfo(
                    file.getId(),
                    file.getFileName(),
                    file.getUserName(),
                    file.getFileSuffix(),
                    file.getDescribtion(),
                    file.getFileType(),
                    //调用数据库存的url，只是url，没有key，所以私有云回返回403：file.getOssUrl()
                    getSignedUrl
            );
        }


    public IPage<FileInfo> getAllFileByLike(String name,String type,int currentPage, int pageSize) {
        // 1. Redis 中按点赞数倒序取 fileId
        IPage<FileInfo> page = new Page<>(currentPage, pageSize);
        List<String> idSet = new ArrayList<>(SredisTemplate.opsForZSet().reverseRange("like:rank", 0, -1));
        if (idSet == null || idSet.isEmpty()) {
            System.out.println("idSet为空");
            // 如果没有点赞数据，返回空结果或默认结果
            return new Page<>();
        }
        Set<Integer> filterIdSet = new LinkedHashSet<>();

        if ((name != null && !name.isEmpty()) || (type != null && !type.isEmpty())) {
            QueryWrapper<FileInfo> filterQw = new QueryWrapper<>();
            if (type != null && !type.isEmpty()) filterQw.eq("file_type", type);
            if (name != null && !name.isEmpty()) filterQw.like("file_name", name);
            filterQw.select("id");//不需要把所有列都查出来，后面只用了 getId()，节省IO和内存
            List<FileInfo> matched = this.list(filterQw);
//            filterIdSet = matched.stream().map(FileInfo::getId).collect(Collectors.toSet());
            for (FileInfo fileInfo : matched){
                filterIdSet.add(fileInfo.getId());
            }
        } else {
            for (String id : idSet) {
                filterIdSet.add(Integer.parseInt(id));
                System.out.println("three");
            }
        }
        // 3. MySQL IN 查询
        QueryWrapper<FileInfo> wrapper = new QueryWrapper<>();
        wrapper.in("id", filterIdSet);
        wrapper.last("ORDER BY FIELD(id," + idSet.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")");
        return this.page(page, wrapper);
    }
    public IPage<FileInfo> getFileByTime(int currentPage, int pageSize, String name, String type){
        IPage<FileInfo> page = new Page<>(currentPage, pageSize);
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
        if (type != null && !type.isEmpty()) queryWrapper.eq("file_type", type);
        if (name != null && !name.isEmpty()) queryWrapper.like("file_name", name);
        queryWrapper.orderByDesc("upload_time"); // 最新在前
        return this.page(page, queryWrapper);
    }

    public String updateByIds(int id, FileInfo fileInfo, HttpServletRequest request) {
        Users loginUser = (Users) request.getAttribute("loginUser");
        // 先查原始记录
        FileInfo fileInfo1 = fileMapper.selectById(id);
        if (fileInfo1 == null) {
            return "文件不存在";
        }
        // 校验权限
        if (loginUser.getUserId() !=(fileInfo1.getUserName())) {
            System.out.println("loginUser.getUserId()"+loginUser.getUserId() +" fileInfo1.getUserName()"+fileInfo1.getUserName());
            return "无权限修改此文件";
        }
//        // 只更新允许修改的字段
//        fileInfo1.setFileName(fileInfo.getFileName());
//        fileInfo1.setDescribtion(fileInfo.getDescribtion());
//        fileMapper.updateById(fileInfo1);
//        return "修改成功";
        UpdateWrapper<FileInfo> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .set("file_name", fileInfo.getFileName())
                .set("describtion", fileInfo.getDescribtion());

        int updated = fileMapper.update(null, wrapper);//entity = null 表示不用实体对象来设置字段
        String oldName = fileInfo1.getFileName();
        String newName = fileInfo.getFileName();

        // 只有文件名真的变了，才操作磁盘
        if (newName != null && !newName.equals(oldName)) {

            String userDir = uploadPath + "\\" + loginUser.getUserName() + "\\";
            String oldFilePath = userDir + oldName + fileInfo1.getFileSuffix();
            String newFilePath = userDir + newName + fileInfo1.getFileSuffix();

            File oldFile = new File(oldFilePath);
            File newFile = new File(newFilePath);

            if (!oldFile.exists()) {
                return "修改失败：原文件不存在";
            }

            try {
                // 重命名（比 copy + delete 更安全）
                boolean renamed = oldFile.renameTo(newFile);
                if (!renamed) {
                    return "修改失败：文件重命名失败";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "修改失败：磁盘操作异常";
            }
        }

        return updated > 0 ? "修改成功" : "修改失败";
    }

    public String deleteByIds(int id, HttpServletRequest request) {
        Users loginUser = (Users) request.getAttribute("loginUser");
        FileInfo fileInfo = fileMapper.selectById(id);
        if (loginUser.getUserId() != fileInfo.getUserName()) {
            return "无权限删除此文件";
        }
        fileMapper.deleteById(id);
        return "删除成功";
    }


}

