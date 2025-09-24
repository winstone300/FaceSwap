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
                        .requestMatchers("/", "/login", "/error", "/favicon.ico", "/h2-console/**","/fap/**").permitAll()
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
                .logout(logout -> logout
                        .logoutUrl("/logout")             // 기본값도 /logout (POST)
                        .logoutSuccessUrl("/login?logout")// 성공 후 이동
                        .invalidateHttpSession(true)      // 세션 무효화
                        .deleteCookies("JSESSIONID")      // 세션 쿠키 삭제
                        .clearAuthentication(true)
                )
                .headers(h -> h.frameOptions(f -> f.sameOrigin()));
        return http.build();
    }
}

