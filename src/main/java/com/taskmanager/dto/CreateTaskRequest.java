package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private LocalDate dueDate;
    @NotNull(message = "Project ID is required")
    private Long projectId;
}