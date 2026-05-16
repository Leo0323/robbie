package com.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vo.FileInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FileService extends IService<FileInfo> {
    String upload(String token,FileInfo fileInfo, String name, MultipartFile file, String describtion);
    String upload2(String token,FileInfo fileInfo, String name, MultipartFile file, String describtion) throws IOException;
//    List<FileInfo> getFileByType(String type);
    IPage<FileInfo> getFileByUserName(String id, String token, int currentPage, int pageSize);
    List<FileInfo> getAllFileByTime();
    IPage<FileInfo> getAllFileByLike(String name,String type,int currentPage, int pageSize);
    void ShowById (HttpServletResponse response, int id) throws IOException;
    FileInfo OssById(int id);
    List<FileInfo> getFileByUserName2(HttpSession session);
    IPage<FileInfo> getFileByTime(int currentPage, int pageSize, String name, String type);
//    Map<String, Object> getFileByTimeOrLike(int currentPage, int pageSize, String sort);
//    <FileInfo>IPage getFileByNameAndType(String name,String type,int currentPage, int pageSize,String sort);
    public String updateByIds(int id, FileInfo fileInfo, HttpServletRequest  request);
    String deleteByIds(int id, HttpServletRequest request);

}
