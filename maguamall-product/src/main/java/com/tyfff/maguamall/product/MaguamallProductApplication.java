package com.tyfff.maguamall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.tyfff.maguamall.product.feign")
public class MaguamallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaguamallProductApplication.class, args);
    }

}
