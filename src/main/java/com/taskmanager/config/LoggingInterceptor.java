package com.taskmanager.config;
import com.taskmanager.dto.LogRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Autowired
    private RabbitTemplate rabbitTemplate; // Replaced RestTemplate

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long responseTime = System.currentTimeMillis() - startTime;

        LogRequest logRequest = new LogRequest();
        logRequest.setEndpoint(request.getRequestURI());
        logRequest.setMethod(request.getMethod());
        logRequest.setStatusCode(response.getStatus());
        logRequest.setResponseTime(responseTime);
        // In a real app, you would get the user ID from the security context
        // logRequest.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            // Send log message to the RabbitMQ exchange
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.LOGGING_EXCHANGE_NAME,
                    RabbitMQConfig.LOGGING_ROUTING_KEY,
                    logRequest
            );
        } catch (Exception e) {
            // Log locally that the logging service is down, but don't fail the main request
            System.err.println("Failed to send log message: " + e.getMessage());
        }
    }
}