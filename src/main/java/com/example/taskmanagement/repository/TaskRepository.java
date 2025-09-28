package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task entity operations
 */
@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    
    /**
     * Find tasks by name containing the given string (case-insensitive)
     * @param name The string to search for in task names
     * @return List of tasks with names containing the search string
     */
    @Query("{'name': {'$regex': ?0, '$options': 'i'}}")
    List<Task> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find task by exact ID
     * @param id The task ID
     * @return Optional Task
     */
    Optional<Task> findById(String id);
    
    /**
     * Find all tasks
     * @return List of all tasks
     */
    List<Task> findAll();
    
    /**
     * Delete task by ID
     * @param id The task ID
     */
    void deleteById(String id);
    
    /**
     * Check if task exists by ID
     * @param id The task ID
     * @return true if task exists, false otherwise
     */
    boolean existsById(String id);
}