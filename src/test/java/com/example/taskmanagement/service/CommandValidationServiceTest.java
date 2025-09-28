package com.example.taskmanagement.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CommandValidationService
 */
@SpringBootTest
public class CommandValidationServiceTest {
    
    private final CommandValidationService validationService = new CommandValidationService();
    
    @Test
    public void testValidCommands() {
        // Test safe commands
        assertTrue(validationService.validateCommand("echo Hello World").isValid());
        assertTrue(validationService.validateCommand("dir").isValid());
        assertTrue(validationService.validateCommand("date /t").isValid());
        assertTrue(validationService.validateCommand("hostname").isValid());
        assertTrue(validationService.validateCommand("whoami").isValid());
    }
    
    @Test
    public void testInvalidCommands() {
        // Test dangerous commands
        assertFalse(validationService.validateCommand("rm -rf /").isValid());
        assertFalse(validationService.validateCommand("del C:\\Windows").isValid());
        assertFalse(validationService.validateCommand("shutdown /s").isValid());
        assertFalse(validationService.validateCommand("sudo rm -rf").isValid());
        assertFalse(validationService.validateCommand("format C:").isValid());
    }
    
    @Test
    public void testDangerousPatterns() {
        // Test dangerous patterns
        assertFalse(validationService.validateCommand("echo test > file.txt").isValid());
        assertFalse(validationService.validateCommand("echo test | rm").isValid());
        assertFalse(validationService.validateCommand("echo test && rm file").isValid());
        assertFalse(validationService.validateCommand("cat ../../../etc/passwd").isValid());
    }
    
    @Test
    public void testEmptyCommand() {
        assertFalse(validationService.validateCommand("").isValid());
        assertFalse(validationService.validateCommand(null).isValid());
        assertFalse(validationService.validateCommand("   ").isValid());
    }
}