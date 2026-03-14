package com.example.Task_Management.repository;

import com.example.Task_Management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Integer> {

    User findById(int id);

    List<User> findByNameStartingWith(String name);

    @Query("select u from User u where lower(u.name) like lower(:name)")
    List<User> findUsers(String name);

}
