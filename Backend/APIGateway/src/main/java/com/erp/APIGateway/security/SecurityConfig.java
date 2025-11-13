package com.erp.APIGateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        // ✅ Allow all auth-related routes through Gateway
                        .pathMatchers(
                                "/api/auth/**",   // allow login, register, validate, etc.
                                "/eureka/**",
                                "/api/attendance",
                                "/admin/**",
                                "/supplier/**",
                                "/employee/**",
                                "/api/billing/**"
                        ).permitAll()
                        // ✅ Everything else needs authentication
                        .anyExchange().authenticated()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        return http.build();
    }
}
