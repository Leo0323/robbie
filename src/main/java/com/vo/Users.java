package com.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName("users")
public class Users implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "userId",type = IdType.AUTO)//必须要加type = IdType.AUTO，否则就算数据库是自增主键，插入时也会变成随机大数字
    private int userId;
    private String userName;
    private String password;
    private String email;
    private String sex;
    @TableField(value = "github_id")
    private String githubId;
    private String avatar;
    private String bio;
    public Users(int userId, String userName, String password, String email, String sex, String githubId, String avatar, String bio) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.sex = sex;
        this.githubId = githubId;
        this.avatar = avatar;
        this.bio = bio;
    }
    public Users(String userName, String password, String email, String sex, String githubId) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.sex = sex;
        this.githubId = githubId;
    }

    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", sex='" + sex + '\'' +
                ", githubId='" + githubId + '\'' +
                ", avatar='" + avatar + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }

    public Users() {
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getGithubId() {
        return githubId;
    }
    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }
}
