package com.personal.backend.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Jjangyushop API", version = "v1", description = "짱규샵 API 명세서"))
@SecurityScheme(
        name = "bearerAuth", // 보안 스킴의 고유한 이름 (2단계에서 사용)
        type = SecuritySchemeType.HTTP, // 인증 타입은 HTTP
        scheme = "bearer", // HTTP 스킴은 'bearer'
        bearerFormat = "JWT", // 베어러 토큰의 형식은 JWT
        in = SecuritySchemeIn.HEADER, // 토큰은 헤더에 담겨 전달됨
        paramName = "Authorization" // 헤더의 이름은 'Authorization'
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/api"));
    }
}
