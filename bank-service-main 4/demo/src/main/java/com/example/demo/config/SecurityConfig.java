package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");
        
        http
       .csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .ignoringRequestMatchers(
        "/api/auth/register",
        "/accounts/**",
        "/customers/**", 
        "/transactions/**",
        "/cards/**",
        "/deposits/**",
        "/loans/**",
        "/credit-cards/**",
        "/exchange/**"
    )
)
.authorizeHttpRequests(authz -> authz
        .requestMatchers("/api/auth/register").permitAll()
        .requestMatchers("/api/users/**").hasRole("ADMIN")
        .requestMatchers("/accounts/**").hasAnyRole("ADMIN", "USER")
        .requestMatchers("/customers/**").hasAnyRole("ADMIN", "USER")
        .requestMatchers("/transactions/**").hasAnyRole("ADMIN", "USER")
        .requestMatchers("/cards/**").hasAnyRole("ADMIN", "USER")
        .requestMatchers("/deposits/**").hasAnyRole("ADMIN", "USER")
        .requestMatchers("/loans/**").hasAnyRole("ADMIN", "USER")
        .requestMatchers("/credit-cards/**").hasAnyRole("ADMIN", "USER")
        .requestMatchers("/exchange/**").hasAnyRole("ADMIN", "USER")
        .anyRequest().authenticated()
    )
            .httpBasic();

        return http.build();
    }
}