package com.spring.springRestDemo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.spring.springRestDemo.payload.auth.TokenDTO;
import com.spring.springRestDemo.payload.auth.UserLoginDTO;
import com.spring.springRestDemo.service.TokenService;
import com.spring.springRestDemo.util.constants.AccountError;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequestMapping("/auth")
@Tag(name="Auth Controller",description="controller for account management")
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    public AuthController(TokenService tokenService,AuthenticationManager authenticationManager){
        this.authenticationManager=authenticationManager;
         this.tokenService=tokenService;
    }
 @PostMapping("/token")
 @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<TokenDTO> token(@RequestBody UserLoginDTO userLogin) throws AuthenticationException {
    try {
        Authentication authentication=authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(userLogin.email(), userLogin.password()));
        return ResponseEntity.ok(new TokenDTO(tokenService.generateToken(authentication)));
    } catch (Exception e) {
        log.debug(AccountError.TOKEN_GENERATION_ERROR.toString()+": "+e.getMessage());
        return new ResponseEntity<>(new TokenDTO(null),HttpStatus.BAD_REQUEST);
    }
     
    }
    
    
}
