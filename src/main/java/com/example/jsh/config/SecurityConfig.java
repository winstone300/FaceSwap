package com.example.jsh.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/h2-console/**", "/gallery/upload", "/gallery/delete"
                ))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error", "/favicon.ico", "/h2-console/**").permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")          // GET 화면
                        .loginProcessingUrl("/login") // POST 처리(시큐리티 필터가 가로챔)
                        // .usernameParameter("username") // 기본값. DB 컬럼이 userId면 "userId"로 바꾸세요.
                        .defaultSuccessUrl("/gallery", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(lo -> lo.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll())
                .headers(h -> h.frameOptions(f -> f.sameOrigin()));
        return http.build();
    }
}

