package com.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dto.CommentDto;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mapper.CommentMapper;
import com.service.CommentService;
import com.vo.Comment;
import com.vo.Users;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper,Comment> implements CommentService {
    @Resource
    private CommentMapper commentMapper;
    public String createComm(Comment  comment) {
        Comment comm = new Comment();
        comm.setUserId(comment.getUserId());
        comm.setPid(comment.getPid());
        comm.setFileId(comment.getFileId());
        comm.setCreateTime(LocalDateTime.now());
        comm.setContent(comment.getContent());
        comm.setReplyUserId(comment.getReplyUserId());
        return this.save(comm)?"success":"fail";
    }
    public IPage<CommentDto> getParComm(int fileId,int currentPage){
        Page<CommentDto> p = new Page<>(currentPage,10);
        MPJLambdaWrapper<Comment> queryWrapper = new MPJLambdaWrapper<Comment>();
        queryWrapper.selectAll(Comment.class);
        //第一个：告诉 MPJ 查哪列
        //第二个：告诉 MyBatis 装进 DTO 哪个属性
        queryWrapper.selectAs(Users::getUserName,CommentDto::getUsername);//告诉返回结果，查询Users的Username的结果封装到CommentDto的username
        //告诉返回结果，查询Users的Username，起别名为replyUser的结果封装到CommentDto的ReplyUserName
        queryWrapper.selectAs("replyUser",Users::getUserName,CommentDto::getReplyUserName);
        queryWrapper.leftJoin(Users.class, Users::getUserId, Comment::getUserId);
        queryWrapper.leftJoin(Users.class,"replyUser", Users::getUserId, Comment::getReplyUserId);
        queryWrapper.eq("file_id",fileId);
        queryWrapper.eq("pid",0);
        queryWrapper.orderByDesc("create_time");
        return commentMapper.selectJoinPage(p,CommentDto.class, queryWrapper);
//        等价于SELECT
//            comment.*,
//            users.user_name AS username,
//            replyUser.user_name AS reply_user_name
//        FROM comment
//        LEFT JOIN users ON users.user_id = comment.user_id
//        LEFT JOIN users AS replyUser ON replyUser.user_id = comment.reply_user_id
//        WHERE file_id = ?
    }
        public IPage<CommentDto> getSubComm(int pid,int fileId,int currentPage){
        IPage< CommentDto> p = new Page<>(currentPage,2);
        MPJLambdaWrapper<Comment> queryWrapper = new MPJLambdaWrapper<Comment>();
        queryWrapper.selectAll(Comment.class);
        queryWrapper.selectAs(Users::getUserName,CommentDto::getUsername);
        queryWrapper.selectAs("replyUser",Users::getUserName,CommentDto::getReplyUserName);
        //User表为主表，User的信息全保留，Comment中可能为null
        queryWrapper.leftJoin(Users.class, Users::getUserId, Comment::getUserId);
        queryWrapper.leftJoin(Users.class,"replyUser",Users::getUserId,Comment::getReplyUserId);
        queryWrapper.eq("file_id",fileId);
        queryWrapper.eq("pid",pid);
        queryWrapper.orderByDesc("create_time");
        return commentMapper.selectJoinPage(p,CommentDto.class, queryWrapper);
    }
    public void deleteComm(Long id){
        this.removeById(id);
    }
    public Long count(int fileId){
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_id",fileId);
        return this.count(queryWrapper);
    }
}
