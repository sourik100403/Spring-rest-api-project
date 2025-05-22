package com.spring.springRestDemo.payload.auth.album;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AlbumPayloadDTO {

    @NotBlank
    @Schema(description = "album name",example="Travel",requiredMode = RequiredMode.REQUIRED)
    private String name;

    @NotBlank
    @Schema(description = "description album",example="description")
    private String description;
}
