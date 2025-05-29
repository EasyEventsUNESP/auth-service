package com.easyevents.auth_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Habilita o Spring Security (se já não estiver habilitado)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/**").permitAll() // Permite acesso público ao seu POST
                        .anyRequest().authenticated() // Qualquer outra requisição exige autenticação
                )
                .csrf(csrf -> csrf.disable()); // Desabilita CSRF para facilitar testes com POST/PUT/DELETE no Postman

        return http.build();
    }
}
