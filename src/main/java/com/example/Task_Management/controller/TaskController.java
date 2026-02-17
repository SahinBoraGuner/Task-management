package com.example.Task_Management.controller;


import com.example.Task_Management.dto.TaskDto;
import com.example.Task_Management.entity.Task;
import com.example.Task_Management.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/{id}")
    public Task findTaskById(@PathVariable("id") Integer id) {
        return taskService.findTaskById(id);
    }

    @GetMapping("/all")
    public List<Task> getAllTasks() {

        return taskService.findAllTasks();
    }

    @PostMapping("/add")
    public Task addTask(@RequestBody TaskDto taskDto) {
        return taskService.addTask(taskDto);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTaskById(@PathVariable("id") Integer id) {
        taskService.deleteTaskById(id);
    }
}
