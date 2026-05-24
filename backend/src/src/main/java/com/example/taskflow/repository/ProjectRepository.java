package com.example.taskflow.repository;

import com.example.taskflow.entity.ProjectEntity;
import com.example.taskflow.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    List<ProjectEntity> findByOwner(UserEntity owner);
}
