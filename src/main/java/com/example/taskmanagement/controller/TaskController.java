package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskExecution;
import com.example.taskmanagement.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for task management operations
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Enable CORS for testing
public class TaskController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    
    @Autowired
    private TaskService taskService;
    
    /**
     * GET /api/tasks - Get all tasks
     * GET /api/tasks?id={taskId} - Get task by ID
     */
    @GetMapping("/tasks")
    public ResponseEntity<?> getTasks(@RequestParam(required = false) String id) {
        try {
            if (id != null && !id.trim().isEmpty()) {
                // Get single task by ID
                logger.info("GET /api/tasks?id={}", id);
                Optional<Task> task = taskService.getTaskById(id.trim());
                
                if (task.isPresent()) {
                    return ResponseEntity.ok(task.get());
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                // Get all tasks
                logger.info("GET /api/tasks");
                List<Task> tasks = taskService.getAllTasks();
                return ResponseEntity.ok(tasks);
            }
        } catch (Exception e) {
            logger.error("Error getting tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving tasks: " + e.getMessage());
        }
    }
    
    /**
     * PUT /api/tasks - Create or update a task
     */
    @PutMapping("/tasks")
    public ResponseEntity<?> createOrUpdateTask(@Valid @RequestBody Task task) {
        try {
            logger.info("PUT /api/tasks - {}", task.getName());
            
            Task savedTask = taskService.saveTask(task);
            return ResponseEntity.ok(savedTask);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error creating/updating task", e);
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating/updating task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving task: " + e.getMessage());
        }
    }
    
    /**
     * DELETE /api/tasks/{id} - Delete a task by ID
     */
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id) {
        try {
            logger.info("DELETE /api/tasks/{}", id);
            
            boolean deleted = taskService.deleteTask(id);
            if (deleted) {
                return ResponseEntity.ok().body("Task deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error deleting task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting task: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/tasks/search?name={searchString} - Find tasks by name
     */
    @GetMapping("/tasks/search")
    public ResponseEntity<?> findTasksByName(@RequestParam String name) {
        try {
            logger.info("GET /api/tasks/search?name={}", name);
            
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Search name parameter is required");
            }
            
            List<Task> tasks = taskService.findTasksByName(name.trim());
            
            if (tasks.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(tasks);
            
        } catch (Exception e) {
            logger.error("Error searching tasks by name", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching tasks: " + e.getMessage());
        }
    }
    
    /**
     * PUT /api/tasks/{id}/execute - Execute a task command
     */
    @PutMapping("/tasks/{id}/execute")
    public ResponseEntity<?> executeTask(@PathVariable String id) {
        try {
            logger.info("PUT /api/tasks/{}/execute", id);
            
            TaskExecution taskExecution = taskService.executeTask(id);
            return ResponseEntity.ok(taskExecution);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Task execution error", e);
            return ResponseEntity.badRequest().body("Execution error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error executing task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error executing task: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/health - Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Task Management API is running");
    }
}