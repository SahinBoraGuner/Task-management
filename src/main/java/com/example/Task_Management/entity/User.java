package com.example.Task_Management.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @Column(name = "Id", length = 45)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "Name", length = 100, nullable = false)
    private String name;

    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    private UserType userType = UserType.USER;

}
