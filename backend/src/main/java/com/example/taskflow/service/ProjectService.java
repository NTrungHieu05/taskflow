package com.example.taskflow.service;

import com.example.taskflow.dto.request.ProjectRequest;
import com.example.taskflow.dto.response.ProjectResponse;
import com.example.taskflow.entity.ProjectEntity;
import com.example.taskflow.entity.UserEntity;
import com.example.taskflow.repository.ProjectRepository;
import com.example.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }

    private ProjectResponse toResponse(ProjectEntity project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setOwnerUsername(project.getOwner().getUsername());
        response.setCreatedAt(project.getCreatedAt());
        return response;
    }

    public ProjectResponse createProject(ProjectRequest request) {
        UserEntity owner = getCurrentUser();
        ProjectEntity project = new ProjectEntity();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwner(owner);
        return toResponse(projectRepository.save(project));
    }

    public List<ProjectResponse> getAllProjects() {
        UserEntity owner = getCurrentUser();
        return projectRepository.findByOwner(owner)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse getById(Long id) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project không tồn tại"));
        return toResponse(project);
    }

    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project không tồn tại"));
        UserEntity currentUser = getCurrentUser();
        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền sửa project này");
        }
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        return toResponse(projectRepository.save(project));
    }

    public void deleteProject(Long id) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project không tồn tại"));
        UserEntity currentUser = getCurrentUser();
        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa project này");
        }
        projectRepository.deleteById(id);
    }
}
