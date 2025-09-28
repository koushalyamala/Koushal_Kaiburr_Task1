package com.example.taskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

/**
 * Main Spring Boot application class for Task Management API
 */
@SpringBootApplication
public class TaskManagementApplication extends AbstractMongoClientConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementApplication.class, args);
    }

    @Override
    protected String getDatabaseName() {
        return "taskmanagement";
    }
}