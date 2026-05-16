package com.dto;

public class UsersDto {
    private int userId;
    private String userName;
    private String token;
    private String githubId;

    public UsersDto() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    public UsersDto(int userId, String userName, String token, String githubId) {
        this.userId = userId;
        this.githubId = githubId;
        this.userName = userName;
        this.token = token;
    }

    public UsersDto(int userId, String userName, String token) {
        this.userId = userId;
        this.userName = userName;
        this.token = token;
    }

    @Override
    public String toString() {
        return "UsersDto{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", token='" + token + '\'' +
                ", githubId='" + githubId + '\'' +
                '}';
    }
}
