package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProjectRequest {
    @NotBlank(message = "Project name cannot be blank")
    private String name;

    private String description;

    @NotNull(message = "Owner ID must be provided")
    private Long ownerId;
}