# Task Management API - Testing Guide

This document provides examples for testing the Task Management REST API using cURL commands and Postman.

## Prerequisites

1. **MongoDB**: Ensure MongoDB is running on `localhost:27017`
2. **Java 17+**: Required to run the Spring Boot application
3. **Maven**: For building the project

## Starting the Application

```bash
# Navigate to project directory
cd C:\Users\ay185064\Downloads\codi

# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080`

## API Endpoints

### 1. Health Check
**GET** `/api/health`

```bash
curl -X GET http://localhost:8080/api/health
```

Expected Response:
```
Task Management API is running
```

### 2. Get All Tasks
**GET** `/api/tasks`

```bash
curl -X GET http://localhost:8080/api/tasks
```

### 3. Get Task by ID
**GET** `/api/tasks?id={taskId}`

```bash
curl -X GET "http://localhost:8080/api/tasks?id=123"
```

### 4. Create/Update Task
**PUT** `/api/tasks`

```bash
curl -X PUT http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "id": "123",
    "name": "Print Hello",
    "owner": "John Smith",
    "command": "echo Hello World!"
  }'
```

#### Example Tasks to Create:

**Echo Command:**
```bash
curl -X PUT http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "id": "123",
    "name": "Print Hello",
    "owner": "John Smith",
    "command": "echo Hello World!"
  }'
```

**List Directory (Windows):**
```bash
curl -X PUT http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "id": "124",
    "name": "List Current Directory",
    "owner": "Jane Doe",
    "command": "dir"
  }'
```

**Date Command (Windows):**
```bash
curl -X PUT http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "id": "125",
    "name": "Show Current Date",
    "owner": "System Admin",
    "command": "date /t"
  }'
```

### 5. Search Tasks by Name
**GET** `/api/tasks/search?name={searchString}`

```bash
curl -X GET "http://localhost:8080/api/tasks/search?name=Hello"
```

### 6. Execute Task
**PUT** `/api/tasks/{id}/execute`

```bash
curl -X PUT http://localhost:8080/api/tasks/123/execute
```

### 7. Delete Task
**DELETE** `/api/tasks/{id}`

```bash
curl -X DELETE http://localhost:8080/api/tasks/125
```

## Security Testing

The API includes command validation to prevent malicious commands. Try this example to see security in action:

```bash
curl -X PUT http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "id": "999",
    "name": "Dangerous Task",
    "owner": "Hacker",
    "command": "rm -rf /"
  }'
```

Expected Response: `400 Bad Request` with validation error message.

## Complete Testing Flow

1. **Start the application**
2. **Check health**: `curl -X GET http://localhost:8080/api/health`
3. **Create a task**: Use the echo command example above
4. **Verify task creation**: `curl -X GET http://localhost:8080/api/tasks`
5. **Execute the task**: `curl -X PUT http://localhost:8080/api/tasks/123/execute`
6. **Check execution results**: `curl -X GET "http://localhost:8080/api/tasks?id=123"`
7. **Search for tasks**: `curl -X GET "http://localhost:8080/api/tasks/search?name=Hello"`
8. **Delete task**: `curl -X DELETE http://localhost:8080/api/tasks/123`

## Sample JSON Response

After creating and executing a task, you should see a response like:

```json
{
  "id": "123",
  "name": "Print Hello",
  "owner": "John Smith",
  "command": "echo Hello World!",
  "taskExecutions": [
    {
      "id": "generated-id",
      "startTime": "2023-04-21T15:51:42.276Z",
      "endTime": "2023-04-21T15:51:43.276Z",
      "output": "Hello World!"
    }
  ]
}
```

## Postman Collection

Import the provided Postman collection file:
`postman/Task_Management_API.postman_collection.json`

This collection includes:
- All API endpoints with example requests
- Pre-configured test data
- Security validation tests
- Complete workflow examples

## Error Handling

The API handles various error scenarios:
- **400 Bad Request**: Invalid input or command validation failure
- **404 Not Found**: Task not found
- **500 Internal Server Error**: Server-side errors

## Logging

Application logs are available in:
- Console output (DEBUG level for com.example.taskmanagement)
- Log file: `logs/task-management-api.log`