package com.example.Task_Management.repository;

import com.example.Task_Management.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepo extends JpaRepository<Task, Integer> {

    Task findById(int id);
}
