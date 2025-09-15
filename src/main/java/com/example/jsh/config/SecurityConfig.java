package com.example.jsh.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
                // 파일 업로드/삭제, H2 콘솔만 CSRF 예외. 로그인 POST는 CSRF 보호 유지
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/h2-console/**", "/images/upload", "/images/*/delete"
                ))

                // 루트/로그인 페이지/정적/H2는 허용, 나머지는 인증
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login","/signin", "/error", "/favicon.ico", "/h2-console/**").permitAll()
                        .requestMatchers(org.springframework.boot.autoconfigure.security.servlet.PathRequest
                                .toStaticResources().atCommonLocations()).permitAll()
                        .anyRequest().authenticated()
                )

                // ★ 페이지와 처리 URL 분리
                .formLogin(login -> login
                        .loginPage("/login")            // GET: 화면만
                        .loginProcessingUrl("/login")  // POST: 인증 처리
                        .defaultSuccessUrl("/gallery", true)
                        .failureUrl("/signin?error")
                        .permitAll()
                )

                .logout(lo -> lo
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/signin?logout")
                        .permitAll()
                )

                // H2 콘솔 frame 허용
                .headers(h -> h.frameOptions(f -> f.sameOrigin()));

        return http.build();
    }
}

