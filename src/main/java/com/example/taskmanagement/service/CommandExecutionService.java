package com.example.taskmanagement.service;

import com.example.taskmanagement.model.TaskExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Service for executing shell commands and capturing their output
 */
@Service
public class CommandExecutionService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommandExecutionService.class);
    private static final int COMMAND_TIMEOUT_SECONDS = 30; // 30 seconds timeout
    
    /**
     * Executes a shell command and returns a TaskExecution with the results
     * @param command The command to execute
     * @return TaskExecution containing execution details and output
     */
    public TaskExecution executeCommand(String command) {
        Date startTime = new Date();
        StringBuilder output = new StringBuilder();
        Date endTime;
        
        try {
            logger.info("Executing command: {}", command);
            
            // Determine the appropriate command executor based on OS
            ProcessBuilder processBuilder = createProcessBuilder(command);
            processBuilder.redirectErrorStream(true); // Combine stdout and stderr
            
            Process process = processBuilder.start();
            
            // Read the output
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            // Wait for process to complete with timeout
            boolean finished = process.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            endTime = new Date();
            
            if (!finished) {
                process.destroyForcibly();
                output.append("Command timed out after ").append(COMMAND_TIMEOUT_SECONDS).append(" seconds");
                logger.warn("Command timed out: {}", command);
            } else {
                int exitCode = process.exitValue();
                if (exitCode != 0) {
                    output.append("Command exited with code: ").append(exitCode);
                    logger.warn("Command failed with exit code {}: {}", exitCode, command);
                }
            }
            
        } catch (IOException e) {
            endTime = new Date();
            output.append("Error executing command: ").append(e.getMessage());
            logger.error("IOException while executing command: {}", command, e);
        } catch (InterruptedException e) {
            endTime = new Date();
            output.append("Command execution was interrupted: ").append(e.getMessage());
            logger.error("InterruptedException while executing command: {}", command, e);
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (Exception e) {
            endTime = new Date();
            output.append("Unexpected error: ").append(e.getMessage());
            logger.error("Unexpected error while executing command: {}", command, e);
        }
        
        TaskExecution taskExecution = new TaskExecution();
        taskExecution.setStartTime(startTime);
        taskExecution.setEndTime(endTime);
        taskExecution.setOutput(output.toString().trim());
        
        logger.info("Command execution completed: {} -> {}", command, taskExecution.getOutput());
        return taskExecution;
    }
    
    /**
     * Creates a ProcessBuilder appropriate for the current operating system
     * @param command The command to execute
     * @return ProcessBuilder configured for the current OS
     */
    private ProcessBuilder createProcessBuilder(String command) {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder;
        
        if (os.contains("win")) {
            // Windows
            processBuilder = new ProcessBuilder("cmd", "/c", command);
        } else {
            // Unix-like systems (Linux, macOS, etc.)
            processBuilder = new ProcessBuilder("sh", "-c", command);
        }
        
        return processBuilder;
    }
}