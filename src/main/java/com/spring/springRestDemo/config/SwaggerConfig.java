package com.spring.springRestDemo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Demo API",
        version = "1.0",
        description = "Spring Boot RESTful API demo by Sourik",
        termsOfService = "",
        contact = @Contact(
            name = "sourikspring",
            email = "sourikparui54@gmail.com",
            url = ""
        ),
        license = @License(
            name = "Apache 2.0",
            url = ""
        )
    )
)
public class SwaggerConfig {
}
