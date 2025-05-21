package com.spring.springRestDemo.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordDTO {
    @NotBlank(message = "Old password is required")
    @Size(min=6,max=20)
    @Schema(description = "password",example="password",requiredMode = RequiredMode.REQUIRED,maxLength = 20,minLength = 6)
    private String oldPassword;
     

    @Size(min=6,max=20)
    @Schema(description = "password",example="password",requiredMode = RequiredMode.REQUIRED,maxLength = 20,minLength = 6)
    @NotBlank(message = "New password is required")
    private String newPassword;
}
