package com.easyevents.auth_service.security;

import com.easyevents.auth_service.domain.model.UsuarioModel;
import com.easyevents.auth_service.repository.UsuarioRepository;
import com.easyevents.auth_service.service.CustomOidcUserService; // Mantenha a importação

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringConfig {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOidcUserService customOidcUserServiceInstance) throws Exception {
        http
//                .authorizeHttpRequests(registry -> {
//                    registry.requestMatchers("/", "/login").permitAll();
//                    registry.requestMatchers("/auth/public/**", "/auth/criar").permitAll();
//                    registry.requestMatchers("/auth/**").authenticated();
//                    registry.anyRequest().authenticated();
//                })
//                .oauth2Login(oauth2 -> oauth2
//                        .userInfoEndpoint(userInfoEndpointConfig ->
//                                // Use a instância de CustomOidcUserService passada como parâmetro
//                                userInfoEndpointConfig.oidcUserService(customOidcUserServiceInstance)
//                        )
//                        .defaultSuccessUrl("/auth/", true)
//                )
//                .formLogin(form -> form
//                        .defaultSuccessUrl("/auth/", true)
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/auth/desconectado") // Certifique-se que este endpoint existe ou use "/"
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                )
                .authorizeHttpRequests(registry -> registry
                        .anyRequest().permitAll() // 1. Permite TODAS as requisições
                )
                .csrf(AbstractHttpConfigurer::disable); // Desabilita CSRF para simplificar, mas considere habilitar em produção;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            UsuarioModel usuarioModel = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário com e-mail '" + email + "' não encontrado."));
            return User.builder()
                    .username(usuarioModel.getEmail())
                    .password(usuarioModel.getSenha())
                    .authorities("ROLE_USER") // Adapte conforme seus papéis/authorities
                    .build();
        };
    }
}