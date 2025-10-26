package com.taskmanager.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Configuration for Notification Service
    public static final String NOTIFICATION_EXCHANGE_NAME = "notification_exchange";
    public static final String NOTIFICATION_QUEUE_NAME = "notification_queue";
    public static final String NOTIFICATION_ROUTING_KEY_PATTERN = "task.#";

    @Bean
    Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE_NAME, false);
    }

    @Bean
    TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE_NAME);
    }

    @Bean
    Binding notificationBinding(Queue notificationQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(NOTIFICATION_ROUTING_KEY_PATTERN);
    }

    // Configuration for Logging Service
    public static final String LOGGING_EXCHANGE_NAME = "logging_exchange";
    public static final String LOGGING_QUEUE_NAME = "logging_queue";
    public static final String LOGGING_ROUTING_KEY = "log.api";

    @Bean
    Queue loggingQueue() {
        return new Queue(LOGGING_QUEUE_NAME, false);
    }

    @Bean
    TopicExchange loggingExchange() {
        return new TopicExchange(LOGGING_EXCHANGE_NAME);
    }

    @Bean
    Binding loggingBinding(Queue loggingQueue, TopicExchange loggingExchange) {
        return BindingBuilder.bind(loggingQueue).to(loggingExchange).with(LOGGING_ROUTING_KEY);
    }
}