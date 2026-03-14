package com.example.Task_Management.controller;


import com.example.Task_Management.dto.UserDto;
import com.example.Task_Management.dto.UserSearchDto;
import com.example.Task_Management.entity.User;
import com.example.Task_Management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public List<User> findAllUsers() {

        return userService.findAllUsers();
    }

    @GetMapping("/search")
    public List<User> findAllUsersByName(@RequestParam String name ) {
        return  userService.findAllUsersByName(name);
    }

    @PostMapping("/search")
    public List<User> findUsersByName(@RequestBody UserSearchDto  userSearchDto) {
        return  userService.findUsersByName(userSearchDto);
    }

    @PostMapping(path = "/add")
    public User addUser(@RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUserById(id);
    }
}
