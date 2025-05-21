package com.spring.springRestDemo.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthoritiesDTO {


    @NotBlank
    @Schema(description = "authorities",example="USER",
    requiredMode = RequiredMode.REQUIRED)
    private String authorities;
    
}
