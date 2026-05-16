package com.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(value = "user_id")
    private int userId;
    @TableField(value = "pid")
    private int pid;
    @TableField(value = "file_id")
    private int fileId;
    @TableField(value = "create_time")
    private LocalDateTime createTime;
    @TableField(value = "content")
    private String content;
    @TableField(value = "reply_user_id")
    private int replyUserId;

    public Comment(int userId, int pid, int fileId, LocalDateTime createTime, String content, int replyUserId) {
        this.id = id;
        this.userId = userId;
        this.pid = pid;
        this.fileId = fileId;
        this.createTime = createTime;
        this.content = content;
        this.replyUserId = replyUserId;
    }

    public Comment() {
    }

    // getter 方法
    public int getUserId() { return userId; }
    public int getPid() { return pid; }
    public int getFileId() { return fileId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public String getContent() { return content; }
    public Long getId() { return id; }
    public int getReplyUserId() {
        return replyUserId;
    }

    // setter 方法
    public void setUserId(int userId) { this.userId = userId; }
    public void setPid(int pid) { this.pid = pid; }
    public void setFileId(int fileId) { this.fileId = fileId; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public void setContent(String content) { this.content = content; }
    public void setId(Long id) { this.id = id; }
    public void setReplyUserId(int replyUserId) {
        this.replyUserId = replyUserId;
    }

    // toString 方法
    @Override
    public String toString() {
        return "FileRecord{" +
                "userId=" + userId +
                ", pid=" + pid +
                ", fileId=" + fileId +
                ", createTime=" + createTime +
                ", content='" + content + '\'' +
                ", id=" + id +
                '}';
    }
}