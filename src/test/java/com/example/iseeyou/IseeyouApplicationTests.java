package com.example.iseeyou;

import com.Util.UploadUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dto.RegisterDTO;
import com.mapper.UserMapper;
import com.service.FileService;
import com.service.LikeService;
import com.service.UserService;
import com.vo.FileInfo;
import com.vo.Users;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
public class IseeyouApplicationTests {
    @Autowired
    private UserMapper userMapper;
    @Resource
    UserService userService;
    @Resource
    private FileService fileservice;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private LikeService likeService;
    @Test
    void TestBaseMapper() {
        Users users = userMapper.selectById(20);
        System.out.println(users.toString());
    }
    //不用Autowired依赖注入的写法：.this
    //    @Test
    //    void TestBaseMapper() {
    //        Users users = this.userMapper.selectById(20);
    //        System.out.println(users.toString());
    //    }
    @Test
    public void subtractProductAndSum() {

        int n = 57185;
        int count = String.valueOf(n).length();
        int mul = 1;
        int sum = 0;
        while(n>9){

            while (count >= 1) {
                int p = (int) Math.pow(10, count-1);
                int a = n / p;
                n = n -a*p;
                System.out.println("a:"+a);
                count--;
                sum = sum+a;
                mul = mul * a;
            }
        }
        System.out.println("sum"+sum);
        System.out.println("mul:"+mul);
        System.out.println(mul -sum);
    }
    @Test
    void TestService() {
        IPage<Users> page = new Page<>(1, 10);
        IPage<Users> userPage = userService.page(page); // 调用 page 方法
        List<Users> userList = userPage.getRecords();
        long total = userPage.getTotal();
        System.out.println("Total users: " + total);
        for (Users user : userList) {
            System.out.println("User: " + user);
        }
    }
    @Test
    void TestService2() {
        Set<Object> idSet = redisTemplate.opsForZSet().reverseRange("like:rank", 0, -1);
        System.out.println("idSet:" + idSet);
        if (idSet == null || idSet.isEmpty()) {
            System.out.println("没有点赞数据");
        }
        System.out.println();
        // 2. 转 Integer
        List<Integer> ids = idSet.stream()
                .map(obj -> {
                    if (obj instanceof Integer) return (Integer) obj;
                    if (obj instanceof String) return Integer.parseInt((String) obj);
                    return Integer.parseInt(obj.toString()); // 保底
                })
                .collect(Collectors.toList());
        System.out.println("ids:" + ids);
        // 3. MySQL IN 查询
        QueryWrapper<FileInfo> wrapper = new QueryWrapper<>();
        wrapper.in("id", ids);
        wrapper.last("ORDER BY FIELD(id," + ids.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")");
    }
    @Test
    void TestService3() {
        Users users = new Users();
        users.setUserName("robert");
        users.setPassword("123456");
        userService.login(users);
    }
    @Test
    void TestService4() {
        IPage<FileInfo> pa = fileservice.getAllFileByLike("1","image",1, 22);
        for (FileInfo fileInfo : pa.getRecords()){
            System.out.println(fileInfo.getId());
        }
//        IPage<FileInfo> pa1 = fileservice.getFileByTime(1, 22,"1", null);
//        for (FileInfo fileInfo : pa1.getRecords()){
//            System.out.println("time:"+fileInfo.getUploadTime());
//        }
    }
    @Test
    void TestService5() {
//        List<FileInfo> fileInfo =likeService.getUserLike(1);
//        for (FileInfo fileInfo1 : fileInfo){
//            System.out.println(fileInfo1.getFileName());
//        }
        int a = (int) Math.pow(8,1.0/3.0);
        System.out.println( a);
    }
    @Test
    void TestService6() {
        RegisterDTO users = new RegisterDTO("robert","123456","123456","123456@qq.com","1");
        String a = users.getPassword();
        System.out.println(a);

    }
    @Test
    void TestService7() throws IOException {
        String content = "test file content";
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                content.getBytes()
        );
        String a = UUID.randomUUID().toString();
        String path = UploadUtil.uploadFile(file,a);
        System.out.println(path);
    }
}
