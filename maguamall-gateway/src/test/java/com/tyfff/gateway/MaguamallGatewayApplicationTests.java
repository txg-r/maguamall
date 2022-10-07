package com.tyfff.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MaguamallGatewayApplicationTests {

    @Value("${test}")
    String te;

    @Test
    void contextLoads() {
        System.out.println(te);
    }

}
