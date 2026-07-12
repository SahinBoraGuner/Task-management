package com.example.Task_Management.service;

import com.example.Task_Management.entity.Task;
import com.example.Task_Management.entity.User;
import com.example.Task_Management.repository.TaskRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class OverdueTaskNotifier {

    @Autowired
    private TaskRepo taskRepo;

    @Scheduled(fixedRate = 10000)
    public void checkOverdueTasks() {
        List<Task> overdueTasks = taskRepo.findOverdueUnnotifiedTasks();

        for (Task task : overdueTasks) {
            User user = task.getUser();
            log.warn("Overdue task detected. Task: '{}' (id={}), dueDate: {}, assignedUser: {} <{}>",
                    task.getTitle(), task.getId(), task.getDueDate(),
                    user != null ? user.getName() : "unassigned",
                    user != null ? user.getEmail() : "n/a");

            task.setOverdueNotifiedAt(Instant.now());
            taskRepo.save(task);
        }

        log.info("Overdue task check complete. Found {} overdue task(s).", overdueTasks.size());
    }
}
