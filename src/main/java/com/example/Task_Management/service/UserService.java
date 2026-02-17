package com.example.Task_Management.service;



import com.example.Task_Management.dto.UserDto;
import com.example.Task_Management.entity.User;
import com.example.Task_Management.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public User addUser(UserDto userDto) {
        log.info("Adding User. User Name: {}", userDto.getName());

        User user = new User();
        user.setName(userDto.getName());
        user.setUserType(userDto.getUserType());
        userRepo.save(user);

        log.info("Added User successfully. User Name: {}",
                user.getName());

        return user;
    }

}
