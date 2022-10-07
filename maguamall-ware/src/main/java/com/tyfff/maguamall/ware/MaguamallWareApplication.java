package com.tyfff.maguamall.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MaguamallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaguamallWareApplication.class, args);
    }

}
