package com.example.taskflow.dto.request;

import com.example.taskflow.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    private String description;
    private TaskStatus status;
    private Long assigneeId;
    private LocalDateTime deadline;
}
