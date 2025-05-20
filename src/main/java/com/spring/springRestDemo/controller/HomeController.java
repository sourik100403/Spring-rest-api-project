package com.spring.springRestDemo.controller;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class HomeController {
    @GetMapping("/")
    public String demo(){
        return "hello world";
    }

    @GetMapping("/test")
    @Tag(name="Test",description="The Test Api")
    @SecurityRequirement(name="sourikspring-demo")
    public String test(){
        return "test api";
    }   
}
