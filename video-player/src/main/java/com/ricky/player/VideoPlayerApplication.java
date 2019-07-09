package com.ricky.player;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.ricky.player.dao")
@ComponentScan("com.ricky.player.*")
public class VideoPlayerApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(VideoPlayerApplication.class, args);
    }

}
