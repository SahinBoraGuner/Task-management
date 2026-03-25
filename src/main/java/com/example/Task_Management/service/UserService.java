package com.example.Task_Management.service;



import com.example.Task_Management.dto.UserDto;
import com.example.Task_Management.dto.UserSearchDto;
import com.example.Task_Management.entity.User;
import com.example.Task_Management.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    public List<User> findAllUsersByName(String name) {

        return userRepo.findUsers( name + '%');
    }

    public List<User> findUsersByName(UserSearchDto userSearchDto) {

        log.info("Finding Users. User Name: {}", userSearchDto.getName());
        List<User> user = userRepo.findUsers(userSearchDto.getName() + '%');
        if (user == null) {
            throw new RuntimeException("Aradığınız kullanıcı bulunamadı.");
        }

        return  user;
    }


    public User addUser(UserDto userDto) {
        log.info("Adding User. User Name: {}", userDto.getName());

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setUserType(userDto.getUserType());
        userRepo.save(user);

        log.info("Added User successfully. User Name: {}",
                user.getName());

        return user;
    }


    public void deleteUserById(Integer id) {
        userRepo.deleteById(id);
    }

}
