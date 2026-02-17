package com.example.Task_Management.service;


import com.example.Task_Management.dto.TaskDto;
import com.example.Task_Management.entity.Task;
import com.example.Task_Management.entity.TaskStatus;
import com.example.Task_Management.repository.TaskRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TaskService {


    @Autowired
    private TaskRepo taskRepo;

    public Task findTaskById(Integer id) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() -> {
                    throw new RuntimeException("Aradığınız görev bulunamadı.");
                });

        return task;
    }

    public List<Task> findAllTasks() {
        return taskRepo.findAll();
    }

    public Task addTask(TaskDto taskDto) {
        log.info("Adding task. Task Title: {}", taskDto.getTitle());

        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setDueDate(taskDto.getDueDate());
        task.setStatus(taskDto.getStatus());
        taskRepo.save(task);

        log.info("Added task successfully. Task Title: {}",
                task.getTitle());

        return task;
    }

    public void deleteTaskById(Integer id) {
        log.info("Deleted task successfully.");
        taskRepo.deleteById(id);
    }

    //Controller a imp et
    public Task updateTask(TaskDto taskDto) {

        log.info("Updating task. Task Title: {}", taskDto.getTitle());

        Task task = taskRepo.findById(taskDto.getId());
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setDueDate(taskDto.getDueDate());
        task.setStatus(taskDto.getStatus());
        taskRepo.save(task);

        log.info("Updated task successfully. Task Title: {}",
                task.getTitle());

        return task;
    }

}
