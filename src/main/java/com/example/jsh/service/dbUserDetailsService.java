package com.example.jsh.service;

import com.example.jsh.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class dbUserDetailsService implements UserDetailsService {
    private final UserAccountRepository users;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var u = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user: " + username));

        // 권한은 roles/authorities 어느 쪽이든 OK. 여기선 roles 사용.
        return User.withUsername(u.getUsername())
                .password(u.getPassword())           // DB의 BCrypt 해시
                .roles(u.getRole().replace("ROLE_","")) // "ROLE_USER" -> "USER"
                .build();
    }
}
