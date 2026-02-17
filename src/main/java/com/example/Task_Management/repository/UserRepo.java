package com.example.Task_Management.repository;

import com.example.Task_Management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {

    User findById(int id);

}
