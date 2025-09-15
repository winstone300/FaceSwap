package com.example.jsh.entity;

import jakarta.persistence.*;

@Entity @Table(name= "user_table")
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length=50)
    private String username;

    @Column(nullable = false)
    private String password;
}
