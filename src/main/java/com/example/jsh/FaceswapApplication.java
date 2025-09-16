package com.example.jsh;

import com.example.jsh.entity.UserAccount;
import com.example.jsh.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
@RequiredArgsConstructor
public class FaceswapApplication implements CommandLineRunner {

	private final DataSource dataSource; // 스프링이 자동으로 주입

	public static void main(String[] args) {
		SpringApplication.run(FaceswapApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try (Connection c = dataSource.getConnection()) {
			System.out.println("✅ H2 connected: " + c.getMetaData().getURL());
		}
	}
	@Bean
	CommandLineRunner seed(UserAccountRepository repo, PasswordEncoder enc) {
		return args -> {
			if (repo.findByUsername("user1").isEmpty()) {
				repo.save(UserAccount.builder()
						.username("user1")
						.password(enc.encode("pass1"))
						.role("ROLE_USER")
						.build());
			}
			if (repo.findByUsername("user2").isEmpty()) {
				repo.save(UserAccount.builder()
						.username("user2")
						.password(enc.encode("pass2"))
						.role("ROLE_USER")
						.build());
			}
		};
	}
}