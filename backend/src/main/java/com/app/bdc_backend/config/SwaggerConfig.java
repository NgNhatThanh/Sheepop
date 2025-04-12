package com.app.bdc_backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Api doc for Sheepop E-commerce"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                )
        }
)
@SecurityScheme(
        name = SwaggerSecurityName.JWT_AUTH,
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@SecurityScheme(
        name = SwaggerSecurityName.REFRESH_TOKEN_AUTH,
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "refresh_token"
)
@Configuration
public class SwaggerConfig {

        @Bean
        public GroupedOpenApi adminApi() {
                return GroupedOpenApi.builder()
                        .group("Admin APIs")
                        .packagesToScan("com.app.bdc_backend.controller.admin")
                        .build();
        }

        @Bean
        public GroupedOpenApi shopApi() {
                return GroupedOpenApi.builder()
                        .group("Shop APIs")
                        .packagesToScan("com.app.bdc_backend.controller.shop")
                        .build();
        }

        @Bean
        public GroupedOpenApi commonApi() {
                return GroupedOpenApi.builder()
                        .group("Common APIs")
                        .packagesToScan("com.app.bdc_backend.controller.common")
                        .build();
        }

}
