@echo off
echo Starting Task Management API...
echo.

echo Checking if MongoDB is running...
tasklist /FI "IMAGENAME eq mongod.exe" 2>NUL | find /I /N "mongod.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo MongoDB is running
) else (
    echo Warning: MongoDB does not appear to be running
    echo Please start MongoDB before running the application
    echo.
)

echo Building the application...
call mvn clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Starting the Spring Boot application...
echo The API will be available at: http://localhost:8080
echo.
echo Available endpoints:
echo   GET  /api/health
echo   GET  /api/tasks
echo   GET  /api/tasks?id={id}
echo   PUT  /api/tasks
echo   DELETE /api/tasks/{id}
echo   GET  /api/tasks/search?name={name}
echo   PUT  /api/tasks/{id}/execute
echo.
echo Press Ctrl+C to stop the application
echo.

call mvn spring-boot:run