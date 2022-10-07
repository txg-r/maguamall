package com.tyfff.maguamall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MaguamallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaguamallMemberApplication.class, args);
    }

}
