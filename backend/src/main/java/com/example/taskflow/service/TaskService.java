package com.example.taskflow.service;
import com.example.taskflow.dto.WebSocketEvent;

import com.example.taskflow.dto.request.TaskRequest;
import com.example.taskflow.dto.response.TaskResponse;
import com.example.taskflow.entity.ProjectEntity;
import com.example.taskflow.entity.TaskEntity;
import com.example.taskflow.entity.TaskStatus;
import com.example.taskflow.entity.UserEntity;
import com.example.taskflow.repository.ProjectRepository;
import com.example.taskflow.repository.TaskRepository;
import com.example.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private TaskResponse toResponse(TaskEntity task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setDeadline(task.getDeadline());
        response.setCreatedAt(task.getCreatedAt());
        if (task.getAssignee() != null) {
            response.setAssigneeUsername(task.getAssignee().getUsername());
        }
        return response;
    }

    public TaskResponse createTask(Long projectId, TaskRequest request) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project không tồn tại"));
        TaskEntity task = new TaskEntity();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO);
        task.setDeadline(request.getDeadline());
        task.setProject(project);
        if (request.getAssigneeId() != null) {
            UserEntity assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));
            task.setAssignee(assignee);
        }
        TaskResponse response = toResponse(taskRepository.save(task));
        messagingTemplate.convertAndSend("/topic/projects/" + projectId,
                new WebSocketEvent("TASK_CREATED", projectId, response.getId(), response.getTitle(), response.getStatus()));
        return response;
    }

    public List<TaskResponse> getByProjectId(Long projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project không tồn tại"));
        return taskRepository.findByProject(project)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse update(Long taskId, TaskRequest request) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task không tồn tại"));
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getDeadline() != null) task.setDeadline(request.getDeadline());
        if (request.getAssigneeId() != null) {
            UserEntity assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));
            task.setAssignee(assignee);
        }
        TaskResponse response = toResponse(taskRepository.save(task));
        messagingTemplate.convertAndSend("/topic/projects/" + task.getProject().getId(),
                new WebSocketEvent("TASK_UPDATED", task.getProject().getId(), taskId, response.getTitle(), response.getStatus()));
        return response;
    }

    public void deleteById(Long taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task không tồn tại"));
        Long projectId = task.getProject().getId();
        taskRepository.deleteById(taskId);
        messagingTemplate.convertAndSend("/topic/projects/" + projectId,
                new WebSocketEvent("TASK_DELETED", projectId, taskId, null, null));
    }
}
