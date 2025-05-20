package com.spring.springRestDemo.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDTO{
     @Email
    @Schema(description = "email address",example="admin@admin.org",requiredMode = RequiredMode.REQUIRED)
    private String email;


    @Size(min=6,max=20)
    @Schema(description = "password",example="Password@10",requiredMode = RequiredMode.REQUIRED,maxLength = 20,minLength = 6)
    private String password;
}
