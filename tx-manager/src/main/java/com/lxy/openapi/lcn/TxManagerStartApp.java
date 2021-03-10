package com.lxy.openapi.lcn;

import com.codingapi.txlcn.tm.config.EnableTransactionManagerServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableTransactionManagerServer//开始事务的协调服务端 独立于eureka
public class TxManagerStartApp {
    public static void main (String[] args){
        SpringApplication.run(TxManagerStartApp.class,args);
    }
}
