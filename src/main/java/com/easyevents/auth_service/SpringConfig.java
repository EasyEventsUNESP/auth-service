package com.easyevents.auth_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/").permitAll(); // Permite acesso à raiz
                    registry.requestMatchers("/auth/public/**").permitAll(); //endpoint público sob /auth
                    registry.requestMatchers("/auth/**").authenticated(); // Todas as outras requisições sob /auth exigem autenticação
                    registry.anyRequest().authenticated(); // Qualquer outra requisição não listada também exige autenticação
                })
                .oauth2Login(oauth2 -> oauth2
                                .defaultSuccessUrl("/auth/", true) // PONTO CHAVE! Redireciona para cá após login OAuth2.
                        //O 'true' força o redirecionamento para esta URL mesmo que o usuário estivesse tentando acessar outra página antes.
                )
                .formLogin(form -> form.defaultSuccessUrl("/auth/", true)); // Redireciona para /auth/ após login com formulário;

        return http.build();
    }
}