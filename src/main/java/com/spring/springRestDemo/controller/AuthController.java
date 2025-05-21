package com.spring.springRestDemo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.spring.springRestDemo.model.Account;
import com.spring.springRestDemo.payload.auth.AccountTDO;
import com.spring.springRestDemo.payload.auth.AccountViewDTO;
import com.spring.springRestDemo.payload.auth.AuthoritiesDTO;
import com.spring.springRestDemo.payload.auth.PasswordDTO;
import com.spring.springRestDemo.payload.auth.ProfileDTO;
import com.spring.springRestDemo.payload.auth.TokenDTO;
import com.spring.springRestDemo.payload.auth.UpdatePasswordDTO;
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
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequestMapping("/api/v1/auth")
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
            accounts.add(new AccountViewDTO(account.getId(),account.getEmail(),account.getAuthorities()));
        }
        return accounts;

    }



//for update authorities role
@PutMapping(value="/users/{user_id}/update-authorities", produces = "application/json",consumes="application/json")
@Operation(summary = "Update user authorities")
@ApiResponse(responseCode = "200", description = "authorities updated successfully")
@ApiResponse(responseCode = "400", description = "Invalid ID")
@ApiResponse(responseCode = "401", description = "token missing")
@ApiResponse(responseCode = "403", description = "token error")
@SecurityRequirement(name = "sourikspring-demo")
public ResponseEntity<AccountViewDTO> update_auth(@Valid @RequestBody AuthoritiesDTO authoritiesDTO,@PathVariable long user_id) {
        Optional<Account> optionalAccount = accountService.findById(user_id);
        if(optionalAccount.isPresent()){
            Account account=optionalAccount.get();
            account.setAuthorities(authoritiesDTO.getAuthorities());
            accountService.save(account);
            AccountViewDTO accountViewDTO=new AccountViewDTO(account.getId(),account.getEmail(),account.getAuthorities());
            return ResponseEntity.ok(accountViewDTO);
        }
        return new ResponseEntity<AccountViewDTO>(new AccountViewDTO(),HttpStatus.BAD_REQUEST);
}


     //get PROFILE
    @GetMapping(value="/profile",produces = "application/json")
    @Operation(summary = "view profile")
    @ApiResponse(responseCode = "401",description = "Token missing")
    @ApiResponse(responseCode = "200",description = "profile")
    @ApiResponse(responseCode = "403",description = "Token error")
    @SecurityRequirement(name="sourikspring-demo") //for passing bearer token
    public ProfileDTO profile(Authentication authentication){
        String email=authentication.getName();
        Optional<Account> optionalAccount=accountService.findByEmail(email);
        Account account=optionalAccount.get();
        ProfileDTO profileDTO=new ProfileDTO(account.getId(), account.getEmail(),account.getAuthorities());
        return profileDTO;


    }
//for update password
@PutMapping(value="/profile/update-password", produces = "application/json",consumes="application/json")
@Operation(summary = "Update profile password")
@ApiResponse(responseCode = "200", description = "Password updated successfully")
@ApiResponse(responseCode = "400", description = "Invalid old password or update failed")
@ApiResponse(responseCode = "401", description = "token missing")
@ApiResponse(responseCode = "403", description = "token error")
@SecurityRequirement(name = "sourikspring-demo")
public AccountViewDTO updatePassword(@Valid @RequestBody PasswordDTO passwordDTO, Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account=optionalAccount.get();
        account.setPassword(passwordDTO.getPassword());
        accountService.save(account);
        AccountViewDTO accountViewDTO=new AccountViewDTO(account.getId(),account.getEmail(),account.getAuthorities());
        return accountViewDTO;

}

//pasword update with old and new pasword
@PostMapping("/updatepassword")
@Operation(summary = "Update user password")
@ApiResponse(responseCode = "200", description = "Password updated successfully")
@ApiResponse(responseCode = "400", description = "Invalid old password or update failed")
@ApiResponse(responseCode = "401", description = "Unauthorized")
@SecurityRequirement(name = "sourikspring-demo")
public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO, Authentication authentication) {
    try {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);

        if (optionalAccount.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
        }

        Account account = optionalAccount.get();

        if (!accountService.matches(updatePasswordDTO.getOldPassword(), account.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect.");
        }

        account.setPassword(updatePasswordDTO.getNewPassword());
        accountService.save(account);

        return ResponseEntity.ok("Password updated successfully.");
    } catch (Exception e) {
        log.error("Error updating password: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update password.");
    }
}



//for delete profile
@DeleteMapping(value="/profile/delete")
@Operation(summary = "Delete Profile")
@ApiResponse(responseCode = "200", description = "Password updated successfully")
@ApiResponse(responseCode = "400", description = "Invalid old password or update failed")
@ApiResponse(responseCode = "401", description = "token missing")
@ApiResponse(responseCode = "403", description = "token error")
@SecurityRequirement(name = "sourikspring-demo")
public ResponseEntity<String> deleteProfile(Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if(optionalAccount.isPresent()){
            accountService.deleteById(optionalAccount.get().getId());
             return ResponseEntity.ok("user delete successfully.");

        }
       return new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);

}
    
}
