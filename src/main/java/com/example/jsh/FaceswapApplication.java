package com.example.jsh;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.RequiredArgsConstructor;

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
}