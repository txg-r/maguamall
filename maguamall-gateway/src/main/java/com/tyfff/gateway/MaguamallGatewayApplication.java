package com.tyfff.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MaguamallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaguamallGatewayApplication.class, args);
    }

}
