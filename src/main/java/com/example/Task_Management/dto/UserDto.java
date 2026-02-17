package com.example.Task_Management.dto;


import com.example.Task_Management.entity.UserType;
import lombok.Data;

@Data
public class UserDto {

    private int id;

    private String name;

    private UserType userType;
}
