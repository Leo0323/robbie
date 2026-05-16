package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vo.Users;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<Users> {
    Users findByGithubId(String githubId);
    @Insert("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(String email);
    @Select("select * from users join file f on users.userId = f.username where f.id = #{id}")
    Users getUserByFileName(int id);

}