package com.dto;

import com.vo.Users;

import java.time.LocalDateTime;

public class CommentDto {
    private Long id;
    private Integer userId;
    private Integer pid;
    private Integer fileId;
    private String content;
    private LocalDateTime createTime;
    private Integer replyUserId;

    // user 表字段
    private String username;
    private String replyUserName;

    public CommentDto() {
    }

    public String getReplyUserName() {
        return replyUserName;
    }

    public void setReplyUserName(String replyUserName) {
        this.replyUserName = replyUserName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(Integer replyUserId) {
        this.replyUserId = replyUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CommentDto(Long id, Integer userId, Integer pid, Integer fileId, String content, LocalDateTime createTime, Integer replyUserId, String username) {
        this.id = id;
        this.userId = userId;
        this.pid = pid;
        this.fileId = fileId;
        this.content = content;
        this.createTime = createTime;
        this.replyUserId = replyUserId;
        this.username = username;
    }

}
