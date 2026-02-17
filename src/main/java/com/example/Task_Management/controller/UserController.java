package com.example.Task_Management.controller;


import com.example.Task_Management.dto.UserDto;
import com.example.Task_Management.entity.User;
import com.example.Task_Management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public User addUser(@RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }
}
