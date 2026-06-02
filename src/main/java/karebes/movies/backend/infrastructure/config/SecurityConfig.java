package karebes.movies.backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Libera o Swagger UI e o OpenAPI JSON/YAML (ajuste se você usa outro path)
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api-docs",
                                "/api-docs/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/index.html**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/actuator/health", "/actuator/info"
                        ).permitAll()
                        // Todas as outras requisições exigem autenticação
                        .anyRequest().authenticated()
                )
                // CSRF: normalmente manter ativo; se precisar permitir POST para swagger-ui (rare), configurar ignoringRequestMatchers
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api-docs/**", "/swagger-ui/**"))
                // Mantém formulário / HTTP Basic (pode trocar conforme sua autenticação JWT/OAuth)
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    // Optional: encoder se você criar users em memória (recomendado em produção usar BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}