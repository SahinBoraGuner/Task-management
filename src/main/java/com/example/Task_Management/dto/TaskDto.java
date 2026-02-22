package com.example.Task_Management.dto;


import com.example.Task_Management.entity.TaskStatus;
import lombok.Data;
import java.util.Date;


@Data
public class TaskDto {

    /**
     * Holds the id of the task
     */
    private int id;

    private int userId;

    private String userName;

    private String title;

    private String description;

    private Date dueDate;

    private TaskStatus status;

}
