package com.example.Task_Management.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id", length = 45)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "user_type", length = 20)
    @Enumerated(EnumType.STRING)
    private UserType userType;

}
