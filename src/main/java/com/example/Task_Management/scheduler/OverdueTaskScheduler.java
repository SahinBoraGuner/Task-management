package com.example.Task_Management.scheduler;

import com.example.Task_Management.entity.Task;
import com.example.Task_Management.entity.TaskStatus;
import com.example.Task_Management.repository.TaskRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class OverdueTaskScheduler {

    private static final List<TaskStatus> EXCLUDED_STATUSES = List.of(TaskStatus.COMPLETED, TaskStatus.CANCELLED);

    @Autowired
    private TaskRepo taskRepo;

    @Scheduled(fixedRate = 10000)
    public void checkOverdueTasks() {
        List<Task> overdueTasks = taskRepo.findOverdueTasks(new Date(), EXCLUDED_STATUSES);

        for (Task task : overdueTasks) {
            try {
                notifyOverdueTask(task);
            } catch (Exception e) {
                log.error("Failed to process overdue task. Task Id: {}", task.getId(), e);
            }
        }
    }

    private void notifyOverdueTask(Task task) {
        log.info("Overdue task detected. Task Id: {}, Title: {}, DueDate: {}",
                task.getId(), task.getTitle(), task.getDueDate());

        // Email sending is intentionally deferred - not implemented yet

        task.setOverdueNotifiedAt(new Date());
        taskRepo.save(task);
    }
}
