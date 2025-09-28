package com.example.taskmanagement.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service for validating shell commands to prevent malicious code execution
 */
@Service
public class CommandValidationService {
    
    // List of dangerous commands that should be blocked
    private static final List<String> DANGEROUS_COMMANDS = Arrays.asList(
        "rm", "rmdir", "del", "format", "fdisk", "mkfs",
        "dd", "shutdown", "reboot", "halt", "poweroff",
        "kill", "killall", "pkill", "sudo", "su",
        "chmod", "chown", "passwd", "useradd", "userdel",
        "crontab", "at", "batch", "systemctl", "service",
        "iptables", "netsh", "reg", "regedit", "gpedit",
        "cmd", "powershell", "bash", "sh", "zsh",
        "nc", "netcat", "telnet", "ssh", "ftp", "wget", "curl"
    );
    
    // Patterns for dangerous operations
    private static final List<Pattern> DANGEROUS_PATTERNS = Arrays.asList(
        Pattern.compile(".*>.*", Pattern.CASE_INSENSITIVE), // File redirection
        Pattern.compile(".*>>.*", Pattern.CASE_INSENSITIVE), // File append
        Pattern.compile(".*\\|.*", Pattern.CASE_INSENSITIVE), // Piping (can be dangerous)
        Pattern.compile(".*&.*", Pattern.CASE_INSENSITIVE), // Background execution
        Pattern.compile(".*;.*", Pattern.CASE_INSENSITIVE), // Command chaining
        Pattern.compile(".*&&.*", Pattern.CASE_INSENSITIVE), // Conditional execution
        Pattern.compile(".*\\|\\|.*", Pattern.CASE_INSENSITIVE), // OR execution
        Pattern.compile(".*`.*`.*", Pattern.CASE_INSENSITIVE), // Command substitution
        Pattern.compile(".*\\$\\(.*\\).*", Pattern.CASE_INSENSITIVE), // Command substitution
        Pattern.compile(".*<.*", Pattern.CASE_INSENSITIVE), // Input redirection
        Pattern.compile(".*/dev/.*", Pattern.CASE_INSENSITIVE), // Device access
        Pattern.compile(".*/proc/.*", Pattern.CASE_INSENSITIVE), // Process filesystem access
        Pattern.compile(".*/sys/.*", Pattern.CASE_INSENSITIVE) // System filesystem access
    );
    
    /**
     * Validates if a command is safe to execute
     * @param command The command to validate
     * @return ValidationResult containing validation status and message
     */
    public ValidationResult validateCommand(String command) {
        if (StringUtils.isBlank(command)) {
            return new ValidationResult(false, "Command cannot be empty");
        }
        
        // Trim and normalize the command
        String normalizedCommand = command.trim().toLowerCase();
        
        // Check for dangerous commands
        for (String dangerousCmd : DANGEROUS_COMMANDS) {
            if (normalizedCommand.startsWith(dangerousCmd + " ") || 
                normalizedCommand.equals(dangerousCmd)) {
                return new ValidationResult(false, 
                    "Command '" + dangerousCmd + "' is not allowed for security reasons");
            }
        }
        
        // Check for dangerous patterns
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(command).matches()) {
                return new ValidationResult(false, 
                    "Command contains dangerous patterns and is not allowed");
            }
        }
        
        // Additional checks for specific dangerous strings
        if (normalizedCommand.contains("../") || normalizedCommand.contains("..\\")) {
            return new ValidationResult(false, 
                "Directory traversal is not allowed");
        }
        
        if (normalizedCommand.contains("/etc/") || normalizedCommand.contains("c:\\windows\\")) {
            return new ValidationResult(false, 
                "Access to system directories is not allowed");
        }
        
        // Command is considered safe
        return new ValidationResult(true, "Command is valid");
    }
    
    /**
     * Inner class representing validation result
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
}