package com.example.craft;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Craft",version = "1.0",description = "The Best Cleaning Service"))
public class CraftApplication {

    public static void main(String[] args) {
        SpringApplication.run(CraftApplication.class, args);
    }

}
