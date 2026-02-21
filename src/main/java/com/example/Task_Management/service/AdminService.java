package com.example.Task_Management.service;


import com.example.Task_Management.dto.TaskDto;
import com.example.Task_Management.entity.Task;
import com.example.Task_Management.entity.TaskStatus;
import com.example.Task_Management.entity.User;
import com.example.Task_Management.repository.TaskRepo;
import com.example.Task_Management.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AdminService {


    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TaskRepo taskRepo;


    public Task createTask(TaskDto taskDto) {
        User foundUser = userRepo.findById(taskDto.getUserId());

        if (foundUser == null) {

            throw new RuntimeException("Aradığınız kullanıcı bulunamadı.");

        }

            Task task = new Task();
            task.setTitle(taskDto.getTitle());
            task.setDescription(taskDto.getDescription());
            task.setDueDate(taskDto.getDueDate());
            task.setUser(foundUser);
            task.setStatus(TaskStatus.READY);
            taskRepo.save(task);

        return task;

    }
}
