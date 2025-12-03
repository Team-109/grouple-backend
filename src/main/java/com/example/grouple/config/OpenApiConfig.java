package com.example.grouple.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Grouple API", version = "1.0"),
        tags = {
                @Tag(name = "01. 인증", description = "인증 관련 Operations"),
                @Tag(name = "02. 유저", description = "유저 관련 Operations"),
                @Tag(name = "03. 조직", description = "조직 관련 Operations"),
                @Tag(name = "04. 조직 회원", description = "조직 회원 관련 Operations"),
                @Tag(name = "05. 조직 가입 요청", description = "조직 회원 관련 Operations"),
                @Tag(name = "06. 조직 공지사항", description = "조직 공지사항 관련 Operations"),
                @Tag(name = "07. 조직 문서", description = "조직 문서 관련 Operations"),
                @Tag(name = "08. 조직 가계부", description = "조직 가계부 관련 Operations"),
                @Tag(name = "09. 조직 일정", description = "조직 일정 관련 Operations")
        }
)
public class OpenApiConfig {
    private static final String BEARER = "bearer";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(BEARER, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER));
    }
}