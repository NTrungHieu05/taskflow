package com.example.taskflow.dto.response;

import com.example.taskflow.entity.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private String assigneeUsername;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
}
