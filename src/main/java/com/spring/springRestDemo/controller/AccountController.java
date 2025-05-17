package com.spring.springRestDemo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class AccountController {
    @GetMapping("/")
    public String demo(){
        return "hello world";
    }

    @GetMapping("/test")
    public String test(){
        return "test api";
    }
    
}
