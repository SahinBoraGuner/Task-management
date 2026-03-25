package com.example.Task_Management.repository;

import com.example.Task_Management.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepo extends JpaRepository<Task, Integer> {

    Task findById(int id);

    @Query("select u from Task u where lower(u.title) like lower(:title)")
    List<Task> findTaskByTitle(String title);

    @Query("select u from Task u where lower(u.description) like lower(:description)")
    List<Task> findTaskByDescription(String description);
}
