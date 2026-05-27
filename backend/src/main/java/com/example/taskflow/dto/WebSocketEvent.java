package com.example.taskflow.dto;

import com.example.taskflow.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketEvent {
    private String type;
    private Long projectId;
    private Long taskId;
    private String title;
    private TaskStatus status;
}
