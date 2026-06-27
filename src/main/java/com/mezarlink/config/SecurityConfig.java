package com.mezarlink.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origin}")
    private String allowedOrigin;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // QR kod akisi sadece GET ile anonim erisim icerdigi icin CSRF'i
                // simdilik kapatiyoruz. Eger ileride cookie tabanli state-changing
                // bir form (cookie ile gonderilen GET disinda istek) eklenirse
                // CSRF token mekanizmasini ayrica kurmamiz gerekecek.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(auth -> auth
                        // Swagger UI / OpenAPI dokumantasyonu herkese acik
                        // (sadece gelistirme ortaminda anlamli, production'da
                        // bu satirlari kaldirmayi veya korumayi dusun).
                        // NOT: "/v3/api-docs" hem tek basina (sonunda / olmadan)
                        // hem de "/v3/api-docs/swagger-config" gibi alt yollarla
                        // cagrilir; "/v3/api-docs/**" deseni sadece alt yollari
                        // kapsar, kok path'in kendisini kapsamaz. Ikisini de
                        // ayri ayri eklemek gerekir.
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // Herkese acik: kayit, giris, public memorial goruntuleme
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        // ONEMLI: /mine, /{slug} desenine gore ONCE tanimlanmali.
                        // Spring path matching'de daha spesifik kural her zaman ilk
                        // eslesen kuraldan once gelmelidir, yoksa "mine" bir slug
                        // gibi yorumlanip yanlislikla herkese acik hale gelir.
                        .requestMatchers(HttpMethod.GET, "/api/memorials/mine").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/memorials/{slug}").permitAll()
                        // Geri kalan her sey giris ister
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(401, "Giris yapmaniz gerekiyor")
                        )
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // "*" KULLANMA: allowCredentials(true) ile birlikte spesifik origin
        // belirtmek zorunludur, aksi halde tarayici cookie'yi reddeder.
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}