package com.example.taskflow.controller;

import com.example.taskflow.dto.request.TaskRequest;
import com.example.taskflow.dto.response.TaskResponse;
import com.example.taskflow.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> create(@PathVariable Long projectId,
                                               @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(projectId, request));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getByProjectId(projectId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long projectId,
                                               @PathVariable Long taskId,
                                               @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.update(taskId, request));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId,
                                       @PathVariable Long taskId) {
        taskService.deleteById(taskId);
        return ResponseEntity.noContent().build();
    }
}
