package com.example.Task_Management.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "taskTab")
public class Task {

    @Id
    @Column(name = "id", length = 45)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", length = 20, nullable = false)
    private String description;

    @Column(name = "dueDate", length = 20, nullable = false)
    private Date dueDate;

    @Column(name = "status", length = 20, nullable = false)
    private TaskStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;



}
