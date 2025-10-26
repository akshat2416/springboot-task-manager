package com.taskmanager.dto;
import lombok.Data;
@Data
public class LogRequest{
    private String endpoint;
    private String method;
    private Integer statusCode;
    private Long responseTime;
    private String userId;
}