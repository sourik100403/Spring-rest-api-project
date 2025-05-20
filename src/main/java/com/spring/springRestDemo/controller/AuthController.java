package com.spring.springRestDemo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.spring.springRestDemo.payload.auth.Token;
import com.spring.springRestDemo.payload.auth.UserLogin;
import com.spring.springRestDemo.service.TokenService;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@RestController
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    public AuthController(TokenService tokenService,AuthenticationManager authenticationManager){
        this.authenticationManager=authenticationManager;
         this.tokenService=tokenService;
    }
    @PostMapping("/token")
    @ResponseBody
    public Token token(@RequestBody UserLogin userLogin) throws AuthenticationException {
        Authentication authentication=authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(userLogin.username(), userLogin.password()));
        return new Token(tokenService.generateToken(authentication));
    }
    
    
}
