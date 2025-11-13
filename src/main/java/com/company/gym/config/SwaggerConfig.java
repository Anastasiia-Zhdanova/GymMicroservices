package com.company.gym.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Gym Management REST API",
                description = "API for managing Trainees, Trainers, and Trainings, using Redis Sessions for security.",
                version = "1.0.0",
                license = @License(name = "Private License")
        )
)
public class SwaggerConfig {

}