package com.taskmanager.dto;

import com.taskmanager.model.TaskStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
    private Long projectId;
    private Long assigneeId;
}