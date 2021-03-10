package com.lxy.openapi.warehouse;


import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.lxy.openapi.warehouse.dao")
@EnableDistributedTransaction // lcn
public class WareHouseApplication {
	public static void main(String[] args) {
		SpringApplication.run(WareHouseApplication.class, args);
	}

}
