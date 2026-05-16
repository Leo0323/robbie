package com.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vo.FileInfo;

import java.util.List;
import java.util.Map;

public interface LikeService {
     String addLike(int beLikedUserId,int likeUserId);
     String cancelLike(int beLikedUserId, int likeUserId);
     int getLikeCount(int beLikedUserId);
     String getLikeUsers(int likeUserId);
     Map getLiKeALL(int id);
     boolean liked(int beLikedUserId, int likeUserId);
     IPage<FileInfo> getUserLike(int UserId,int currentPage, int pageSize);
}
