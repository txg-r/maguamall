package com.tyfff.maguamall.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MaguamallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaguamallProductApplication.class, args);
    }

}
