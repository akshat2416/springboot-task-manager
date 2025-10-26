package com.taskmanager.dto;
import lombok.Data;
import java.util.Set;

@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Set<Long> memberIds;
}