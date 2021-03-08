package com.lxy.openplatform.author;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author Lvxy
 * @Desc
 */
@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class AuthorStartApp {
    public static void main(String[] args) {
        SpringApplication.run(AuthorStartApp.class,args);
    }
}
