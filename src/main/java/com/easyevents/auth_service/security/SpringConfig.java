package com.easyevents.auth_service.security;

import com.easyevents.auth_service.domain.model.UsuarioModel;
import com.easyevents.auth_service.repository.UsuarioRepository;
import com.easyevents.auth_service.service.CustomOidcUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers(
                            "/",
                            "/error",
                            "/auth/login",
                            "/auth/criar",
                            "/auth/public/**",
                            "/auth/senha-temp/**",  // Adicionando o novo endpoint
                            "/oauth2/authorization/**",
                            "/login/oauth2/code/**"
                    ).permitAll();

                    registry.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    registry.requestMatchers("/auth/**").authenticated();
                    registry.anyRequest().authenticated();
                })

                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig.oidcUserService(customOidcUserServiceInstance)
                        )
                        .defaultSuccessUrl("http://localhost:5173/homePage", true)
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("http://localhost:5173/homePage", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("http://localhost:5173/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

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
                    .authorities("ROLE_USER")
                    .build();
        };
    }
}