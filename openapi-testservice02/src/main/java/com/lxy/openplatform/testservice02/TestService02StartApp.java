package com.lxy.openplatform.testservice02;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TestService02StartApp {

    public static void main (String[] args){
        SpringApplication.run(TestService02StartApp.class,args);
    }
}
