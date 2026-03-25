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

    @GetMapping("/search")
    public List<Task> findAllTasksByDescription(@RequestParam String description) {
        return taskService.findAllTasksByDescription(description);
    }

    @GetMapping("/searched")
    public List<Task> findAllTasksByTitle(@RequestParam String title) {
        return taskService.findAllTasksByTitle(title);
    }

    @GetMapping("/all")
    public List<TaskDto> getAllTasks() {
        List<Task> allTasks = taskService.findAllTasks();
        List<TaskDto> taskDtos = allTasks.stream()
                .map(task -> {
                    TaskDto taskDto  = new TaskDto();
                    taskDto.setId(task.getId());
                    taskDto.setTitle(task.getTitle());
                    taskDto.setDescription(task.getDescription());
                    taskDto.setDueDate(task.getDueDate());
                    taskDto.setStatus(task.getStatus());
                    taskDto.setUserId(task.getUser().getId());
                    taskDto.setUserName(task.getUser().getName());
                    return taskDto;
                })
                .toList();

        return taskDtos;
    }

    @PostMapping(path = "/add")
    public Task addTask(@RequestBody TaskDto taskDto) {
        return taskService.addTask(taskDto);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTaskById(@PathVariable("id") Integer id) {
        taskService.deleteTaskById(id);
    }

    @PostMapping("/update")
    public Task updateTask(@RequestBody TaskDto taskDto) {
        return taskService.updateTask(taskDto);
    }
}
