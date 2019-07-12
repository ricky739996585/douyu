package com.ricky.danmu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.ricky.danmu.dao")
@ComponentScan("com.ricky.danmu.*")
public class DanmuApplication {

    public static void main(String[] args) {
        SpringApplication.run(DanmuApplication.class, args);
    }

}
