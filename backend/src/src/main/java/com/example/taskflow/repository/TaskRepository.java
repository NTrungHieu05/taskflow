package com.example.taskflow.repository;

import com.example.taskflow.entity.ProjectEntity;
import com.example.taskflow.entity.TaskEntity;
import com.example.taskflow.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByProject(ProjectEntity project);
    List<TaskEntity> findByProjectAndStatus(ProjectEntity project, TaskStatus status);
}
