package com.example.jsh.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name= "user_table")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length=50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String role;
}
