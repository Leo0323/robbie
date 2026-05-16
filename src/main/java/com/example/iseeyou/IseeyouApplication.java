package com.example.iseeyou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.contro", "com.service", "com.config","com.example.iseeyou","com.handler"})
@MapperScan("com.mapper")
public class IseeyouApplication {
    public static void main(String[] args) {
        SpringApplication.run(IseeyouApplication.class, args);
    }

}
