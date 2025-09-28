package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskExecution;
import com.example.taskmanagement.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for task management operations
 */
@Service
public class TaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private CommandValidationService commandValidationService;
    
    @Autowired
    private CommandExecutionService commandExecutionService;
    
    /**
     * Get all tasks
     * @return List of all tasks
     */
    public List<Task> getAllTasks() {
        logger.info("Retrieving all tasks");
        return taskRepository.findAll();
    }
    
    /**
     * Get task by ID
     * @param id Task ID
     * @return Optional Task
     */
    public Optional<Task> getTaskById(String id) {
        logger.info("Retrieving task by ID: {}", id);
        return taskRepository.findById(id);
    }
    
    /**
     * Find tasks by name containing the search string
     * @param name Search string
     * @return List of matching tasks
     */
    public List<Task> findTasksByName(String name) {
        logger.info("Searching tasks by name: {}", name);
        return taskRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Create or update a task
     * @param task Task to save
     * @return Saved task
     * @throws IllegalArgumentException if command validation fails
     */
    public Task saveTask(Task task) throws IllegalArgumentException {
        logger.info("Saving task: {}", task.getName());
        
        // Validate the command
        CommandValidationService.ValidationResult validationResult = 
            commandValidationService.validateCommand(task.getCommand());
        
        if (!validationResult.isValid()) {
            logger.warn("Command validation failed for task {}: {}", 
                       task.getName(), validationResult.getMessage());
            throw new IllegalArgumentException("Command validation failed: " + validationResult.getMessage());
        }
        
        // Ensure taskExecutions list is initialized
        if (task.getTaskExecutions() == null) {
            task.setTaskExecutions(List.of());
        }
        
        Task savedTask = taskRepository.save(task);
        logger.info("Task saved successfully with ID: {}", savedTask.getId());
        return savedTask;
    }
    
    /**
     * Delete task by ID
     * @param id Task ID
     * @return true if task was deleted, false if task was not found
     */
    public boolean deleteTask(String id) {
        logger.info("Deleting task with ID: {}", id);
        
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            logger.info("Task deleted successfully: {}", id);
            return true;
        } else {
            logger.warn("Task not found for deletion: {}", id);
            return false;
        }
    }
    
    /**
     * Execute a task command and add the execution to the task
     * @param taskId Task ID
     * @return TaskExecution result
     * @throws IllegalArgumentException if task is not found
     */
    public TaskExecution executeTask(String taskId) throws IllegalArgumentException {
        logger.info("Executing task with ID: {}", taskId);
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            logger.warn("Task not found for execution: {}", taskId);
            throw new IllegalArgumentException("Task not found with ID: " + taskId);
        }
        
        Task task = taskOpt.get();
        
        // Validate command again before execution (security check)
        CommandValidationService.ValidationResult validationResult = 
            commandValidationService.validateCommand(task.getCommand());
        
        if (!validationResult.isValid()) {
            logger.warn("Command validation failed during execution for task {}: {}", 
                       taskId, validationResult.getMessage());
            throw new IllegalArgumentException("Command validation failed: " + validationResult.getMessage());
        }
        
        // Execute the command
        TaskExecution taskExecution = commandExecutionService.executeCommand(task.getCommand());
        
        // Add execution to task
        task.addTaskExecution(taskExecution);
        
        // Save updated task
        taskRepository.save(task);
        
        logger.info("Task execution completed for task {}: {}", taskId, taskExecution.getOutput());
        return taskExecution;
    }
    
    /**
     * Check if task exists by ID
     * @param id Task ID
     * @return true if task exists, false otherwise
     */
    public boolean taskExists(String id) {
        return taskRepository.existsById(id);
    }
}