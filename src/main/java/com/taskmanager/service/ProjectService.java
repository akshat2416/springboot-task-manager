package com.taskmanager.service;


import com.taskmanager.dto.CreateProjectRequest;
import com.taskmanager.dto.ProjectDto;
import com.taskmanager.exception.BadRequestException;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.model.Project;
import com.taskmanager.model.User;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    public ProjectDto createProject(CreateProjectRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getOwnerId()));

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwner(owner);
        project.getMembers().add(owner); // Owner is a member by default

        Project savedProject = projectRepository.save(project);
        return mapToDto(savedProject);
    }

    public ProjectDto getProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        return mapToDto(project);
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public ProjectDto addMemberToProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (project.getMembers().contains(user)) {
            throw new BadRequestException("User is already a member of this project.");
        }

        project.getMembers().add(user);
        Project updatedProject = projectRepository.save(project);
        return mapToDto(updatedProject);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        projectRepository.delete(project);
    }

    private ProjectDto mapToDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setOwnerId(project.getOwner().getId());
        dto.setMemberIds(project.getMembers().stream().map(User::getId).collect(Collectors.toSet()));
        return dto;
    }
}