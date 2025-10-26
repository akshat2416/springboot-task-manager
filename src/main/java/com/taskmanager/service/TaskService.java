package com.taskmanager.service;


import com.taskmanager.config.RabbitMQConfig;
import com.taskmanager.dto.CreateTaskRequest;
import com.taskmanager.dto.NotificationRequest;
import com.taskmanager.dto.TaskDto;
import com.taskmanager.dto.UpdateTaskStatusRequest;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.model.Project;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.model.User;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate; // Replaced RestTemplate

    @Transactional
    public TaskDto createTask(CreateTaskRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setProject(project);
        task.setStatus(TaskStatus.TO_DO);

        Task savedTask = taskRepository.save(task);

        // Send notification via RabbitMQ
        String message = String.format("New task '%s' has been created in project '%s'.", savedTask.getTitle(), project.getName());
        sendNotification("TaskCreated", savedTask.getId(), message);

        return mapToDto(savedTask);
    }

    @Transactional
    public TaskDto updateTaskStatus(Long taskId, UpdateTaskStatusRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        task.setStatus(request.getStatus());
        Task updatedTask = taskRepository.save(task);

        // Send notification if task is completed
        if (updatedTask.getStatus() == TaskStatus.DONE) {
            String message = String.format("Task '%s' in project '%s' has been completed.", updatedTask.getTitle(), task.getProject().getName());
            sendNotification("TaskCompleted", updatedTask.getId(), message);
        }

        return mapToDto(updatedTask);
    }

    private void sendNotification(String eventType, Long taskId, String message) {
        try {
            NotificationRequest notificationRequest = new NotificationRequest(eventType, taskId, message);
            // Use RabbitTemplate to send the message to the exchange with a routing key
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE_NAME,
                    "task." + eventType.toLowerCase(),
                    notificationRequest
            );
        } catch (Exception e) {
            // Log locally that sending the message failed
            System.err.println("Failed to send notification message: " + e.getMessage());
        }
    }

    public TaskDto getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        return mapToDto(task);
    }

    @Transactional
    public TaskDto assignTaskToUser(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        task.setAssignee(user);
        Task savedTask = taskRepository.save(task);
        return mapToDto(savedTask);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        taskRepository.delete(task);
    }

    private TaskDto mapToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setDueDate(task.getDueDate());
        dto.setProjectId(task.getProject().getId());
        if (task.getAssignee() != null) {
            dto.setAssigneeId(task.getAssignee().getId());
        }
        return dto;
    }
}