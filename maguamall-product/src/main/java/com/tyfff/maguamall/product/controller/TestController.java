package com.tyfff.maguamall.product.controller;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @PutMapping("/product/test")
    public String test(){
        return "ok";
    }
}
