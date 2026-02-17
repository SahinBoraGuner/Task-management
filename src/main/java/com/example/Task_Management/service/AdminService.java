package com.example.Task_Management.service;


import com.example.Task_Management.dto.TaskDto;
import com.example.Task_Management.entity.Task;
import com.example.Task_Management.entity.TaskStatus;
import com.example.Task_Management.entity.User;
import com.example.Task_Management.repository.AdminRepo;
import com.example.Task_Management.repository.TaskRepo;
import com.example.Task_Management.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TaskRepo taskRepo;


    public Task createTask(TaskDto taskDto) {
        Task foundUser = taskRepo.findById(taskDto.getUserId());
        if (foundUser == null) {

            throw new RuntimeException("Aradığınız kullanıcı bulunamadı.");

        }

        // TO-DO: eğer kullanıcı yoksa kullanıcı bulunamadı diye hata at
        // TO-DO: eğer kullanıcı bulunduysa task veritabanı modelini üret ve kaydet geriyede
        // kaydedilen modelin id sini dön
        if (foundUser != null) {

            Task task = new Task();
            task.setTitle(taskDto.getTitle());
            task.setDescription(taskDto.getDescription());
            task.setDueDate(taskDto.getDueDate());
            task.setStatus(TaskStatus.READY);
            taskRepo.save(task);
        }
        return foundUser;

    }
}
