package com.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mapper.FileMapper;
import com.service.FileService;
import com.service.LikeService;
import com.vo.FileInfo;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class LikeServiceImpl implements LikeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private FileService fileService;
    // 点赞key前缀
    private final String LIKE_COUNT_KEY = "like:count:user:";//被点赞文件的赞数
    // 点赞用户记录key前缀 (Set类型：存点赞人的ID，自动去重)
    private final String LIKE_USER_KEY = "like:users:user:";//该文件都被谁点赞了
    private final String LIKE_USERLike_All = "like:userlike:user:";//用户点赞的所有文件

    // ========== 带防重复点赞的【点赞+1】 ==========
    // param1: beLikedUserId → 被点赞的用户ID（比如userId=1）
    // param2: likeUserId → 点赞的用户ID（比如userId=2，是我给别人点赞）
    public boolean liked(int beLikedUserId, int likeUserId) {
        String countKey = LIKE_COUNT_KEY + beLikedUserId;
        String userKey = LIKE_USER_KEY + beLikedUserId;
        boolean isLiked = stringRedisTemplate.opsForSet()
                .isMember(userKey, String.valueOf(likeUserId));
        if (Boolean.TRUE.equals(isLiked)) {
            return true;
        }
        return false;
    }
    public String addLike(int beLikedUserId,int likeUserId) {
        System.out.println("进入点赞");
        String countKey = LIKE_COUNT_KEY + beLikedUserId;
        String userKey = LIKE_USER_KEY + beLikedUserId;
        String userlikeall = LIKE_USERLike_All + likeUserId;
        stringRedisTemplate.opsForSet().add(userlikeall, String.valueOf(beLikedUserId));
        boolean isLiked = stringRedisTemplate.opsForSet()
                //userKey是"like:file:123"字符串，likeUserId是要判断like:file:123下的任何一个id
                //isMember()判断like:file:123下是否有likeUserId
                .isMember(userKey, String.valueOf(likeUserId));
        if (Boolean.TRUE.equals(isLiked)) {
            return " user:"+beLikedUserId+", 点赞量："+getLikeCount(beLikedUserId)+", 点赞用户："+getLikeUsers(beLikedUserId);
        }
        Long a = stringRedisTemplate.opsForValue().increment(countKey);//opsForXXX()是选择要操作的Redis数据类型
        stringRedisTemplate.opsForSet().add(userKey, String.valueOf(likeUserId));
        stringRedisTemplate.opsForZSet()
                .add("like:rank", String.valueOf(beLikedUserId), a.doubleValue());
        System.out.println(" user:"+beLikedUserId+", 点赞成功！点赞量："+getLikeCount(beLikedUserId)+", 点赞用户："+getLikeUsers(beLikedUserId));
        return " user:"+beLikedUserId+", 点赞成功！点赞量："+getLikeCount(beLikedUserId)+", 点赞用户："+getLikeUsers(beLikedUserId);
    }

    // ========== 取消点赞（连带删除点赞记录） ==========
    public String cancelLike(int beLikedUserId,int likeUserId) {
        String countKey = LIKE_COUNT_KEY + beLikedUserId;
        String userKey = LIKE_USER_KEY + beLikedUserId;
        String userlikeall = LIKE_USERLike_All + likeUserId;
        boolean isCancel = stringRedisTemplate.opsForSet().isMember(userKey, String.valueOf(likeUserId));
        if (Boolean.FALSE.equals(isCancel)){
            return " user:"+beLikedUserId+", 点赞量："+getLikeCount(beLikedUserId)+", 点赞用户："+getLikeUsers(beLikedUserId);
        }
        Long a = stringRedisTemplate.opsForValue().decrement(countKey);
        stringRedisTemplate.opsForSet().remove(userKey,String.valueOf(likeUserId));
        stringRedisTemplate.opsForZSet().incrementScore("like:rank", String.valueOf(beLikedUserId), -1);
        stringRedisTemplate.opsForSet().remove(userlikeall, String.valueOf(beLikedUserId));
        System.out.println("取消点赞");
        return " user:"+beLikedUserId+", 取消点赞成功！点赞量："+getLikeCount(beLikedUserId)+", 点赞用户："+getLikeUsers(beLikedUserId);
    }


    // 原有查询点赞量方法不变
    @Override
    public int getLikeCount(int userId) {
        String countKey = LIKE_COUNT_KEY + userId;
        String countStr = stringRedisTemplate.opsForValue().get(countKey);
        // 核心修复：三目运算符，null就返回0，否则转数字
        return countStr == null ? 0 : Integer.parseInt(countStr);
    }
    public String getLikeUsers(int userId){
        String redisKey = LIKE_USER_KEY + userId;
        return stringRedisTemplate.opsForSet().members(redisKey).toString();
    }
    public Map getLiKeALL(int username){
        Map map = new HashMap();
        map.put("likeCount",this.getLikeCount( username));
        map.put("likeUsers",this.getLikeUsers( username));
        return map;
    }
    public IPage<FileInfo> getUserLike(int userId,int currentPage ,int pageSize){
        IPage<FileInfo> page = new Page<>(currentPage,pageSize);
        String redisKey = LIKE_USERLike_All + userId;
        Set<String> a = stringRedisTemplate.opsForSet().members(redisKey);
        System.out.println( a);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in("id",a);
        return fileService.page(page,queryWrapper);
    }
}