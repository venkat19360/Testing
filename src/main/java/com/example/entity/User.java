package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String password;
    private String phoneNumber;
    private String email;
    private String state;
    private String city;
    private String address;
    private String gender;
    private String role;

    // Getters and setters
    // Constructors
}

