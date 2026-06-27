package com.mezarlink.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mezarlinkOpenApi() {
        // JSESSIONID cookie'sini Swagger UI'da "Authorize" ile test edebilmek icin.
        // Login endpoint'ini Swagger'dan calistirip sonraki istekler otomatik
        // cookie'yi tasir (Swagger UI ayni origin'den calisirsa).
        SecurityScheme sessionCookieScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("JSESSIONID");

        return new OpenAPI()
                .info(new Info()
                        .title("Mezarlink API")
                        .description("Mezar taşlarına yapıştırılan QR kodların yönlendirdiği "
                                + "dijital anı sayfaları için backend API")
                        .version("v0.0.1"))
                .components(new Components().addSecuritySchemes("sessionCookie", sessionCookieScheme));
    }
}
