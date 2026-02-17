package com.example.Task_Management.controller;


import com.example.Task_Management.dto.TaskDto;
import com.example.Task_Management.entity.Task;
import com.example.Task_Management.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
public class AdminController {
    //TO-DO: controller haline çevir
    // createTask metodunu bağla ve postman üzerinden istek atılır hale getir

    @Autowired
    private AdminService adminService;

    @PostMapping
    public Task createTask(@RequestBody TaskDto taskDto) {
        return  adminService.createTask(taskDto);

    }
}
