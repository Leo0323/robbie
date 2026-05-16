package com.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.CommentDto;
import com.vo.Comment;
import org.springframework.stereotype.Service;

@Service
public interface CommentService extends IService<Comment> {
    String createComm(Comment  comment);
    IPage<CommentDto> getParComm(int fileId,int pageNum);
    IPage<CommentDto> getSubComm(int pid,int fileId,int pageNum);
    void deleteComm(Long id);
    Long count(int fileId);
}
