package com.vo;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

@TableName("file")
public class FileInfo {
    @TableId(value = "id",type = IdType.AUTO)
    private int id;
    @TableField("file_name")
    private String fileName;
    @TableField("username")
    private int userName;
    @TableField("file_suffix")
    private String fileSuffix;
    @TableField("describtion")
    private String describtion;
    @TableField("file_type")
    private String fileType;
    @TableField(value = "upload_time",fill = FieldFill.INSERT)
    private LocalDateTime uploadTime = LocalDateTime.now();
    @TableField(value = "oss_url")
    private String ossUrl;
    public FileInfo(int id, String fileName, int userName, String fileSuffix, String describtion, String fileType, String ossUrl) {
        this.id = id;
        this.fileName = fileName;
        this.userName = userName;
        this.fileSuffix = fileSuffix;
        this.describtion = describtion;
        this.fileType = fileType;
        this.uploadTime = LocalDateTime.now();
        this.ossUrl = ossUrl;
    }
    public FileInfo( String fileName, int userName, String fileSuffix, String describtion, String fileType, String ossUrl) {
        this.fileName = fileName;
        this.userName = userName;
        this.fileSuffix = fileSuffix;
        this.describtion = describtion;
        this.fileType = fileType;
        this.uploadTime = LocalDateTime.now();
        this.ossUrl = ossUrl;
    }



    public FileInfo() {
    }

    public String getOssUrl() {
        return ossUrl;
    }

    public void setOssUrl(String ossUrl) {
        this.ossUrl = ossUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public int getUserName() {
        return userName;
    }

    public void setUserName(int userName) {
        this.userName = userName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public String getDescribtion() {
        return describtion;
    }

    public void setDescribtion(String describtion) {
        this.describtion = describtion;
    }
}
