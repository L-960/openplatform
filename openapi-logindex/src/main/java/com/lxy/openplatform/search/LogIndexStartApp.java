package com.lxy.openplatform.search;

import com.lxy.openplatform.search.mq.ReceiveGateWayLogStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableDiscoveryClient
@EnableBinding({ReceiveGateWayLogStream.class})
public class LogIndexStartApp {
    public static void main (String[] args){
        SpringApplication.run(LogIndexStartApp.class,args);
    }
}
