package com.spring.springRestDemo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.spring.springRestDemo.model.Account;
import com.spring.springRestDemo.payload.auth.AccountTDO;
import com.spring.springRestDemo.payload.auth.AccountViewDTO;
import com.spring.springRestDemo.payload.auth.TokenDTO;
import com.spring.springRestDemo.payload.auth.UserLoginDTO;
import com.spring.springRestDemo.service.AccountService;
import com.spring.springRestDemo.service.TokenService;
import com.spring.springRestDemo.util.constants.AccountError;
import com.spring.springRestDemo.util.constants.AccountSuccess;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequestMapping("/auth")
@Tag(name="Auth Controller",description="controller for account management")
@Slf4j
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AccountService accountService;
    
    public AuthController(TokenService tokenService,AuthenticationManager authenticationManager){
        this.authenticationManager=authenticationManager;
         this.tokenService=tokenService;
    }
 @PostMapping("/token")
 @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<TokenDTO> token(@Valid @RequestBody UserLoginDTO userLogin) throws AuthenticationException {
    try {
        Authentication authentication=authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));
        return ResponseEntity.ok(new TokenDTO(tokenService.generateToken(authentication)));
    } catch (Exception e) {
        log.debug(AccountError.TOKEN_GENERATION_ERROR.toString()+": "+e.getMessage());
        return new ResponseEntity<>(new TokenDTO(null),HttpStatus.BAD_REQUEST);
    }
     
}
    

    //add user api
    @PostMapping(value="/users/add",produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "400",description = "please enter a valid email and password length between 6 to 20 character")
    @ApiResponse(responseCode = "200",description = "account added")
    @Operation(summary = "Add a new user")
    public ResponseEntity<String> addUser(@Valid @RequestBody AccountTDO accountTDO){
        try{
            Account account=new Account();
            account.setEmail(accountTDO.getEmail());
            account.setPassword(accountTDO.getPassword());
            accountService.save(account);
            return ResponseEntity.ok(AccountSuccess.ACCOUNT_ADDED.toString());
        }catch(Exception e){
            log.debug(AccountError.ADD_ACCOUNT_ERROR.toString()+": "+e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        }

    }

     //get all user api
    @GetMapping(value="/users",produces = "application/json")
    @Operation(summary = "List user api")
    @ApiResponse(responseCode = "401",description = "Token missing")
    @ApiResponse(responseCode = "200",description = "List of users")
    @ApiResponse(responseCode = "403",description = "Token error")
    @SecurityRequirement(name="sourikspring-demo") //for passing bearer token
    public List<AccountViewDTO> users(){
        List<AccountViewDTO> accounts=new ArrayList<>();
        for(Account account:accountService.findall()){
            accounts.add(new AccountViewDTO(account.getId(),account.getEmail(),account.getRole()));
        }
        return accounts;

    }
    
}
