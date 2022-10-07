package com.tyfff.maguamall.product;


import com.tyfff.maguamall.product.entity.BrandEntity;
import com.tyfff.maguamall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MaguamallProductApplicationTests {
    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {
    }

}
