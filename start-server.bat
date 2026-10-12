@echo off
title ShopEasy Ecommerce Server
echo ========================================================
echo   Starting ShopEasy Spring Boot Web Application
echo ========================================================
echo.
cd /d "%~dp0"

:: Automatically stop any leftover process listening on port 8081
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8081" ^| findstr "LISTENING"') do (
    echo Freeing port 8081 from previous process (PID %%a)...
    taskkill /F /PID %%a >nul 2>&1
)

echo.
if exist "target\shopeasy-backend-1.0.0.jar" (
    echo Starting server on http://localhost:8081 ...
    echo Open your browser to: http://localhost:8081
    echo Press Ctrl+C in this window to stop the server anytime.
    echo.
    java -jar target\shopeasy-backend-1.0.0.jar
) else (
    echo Starting server with Maven Wrapper on http://localhost:8081 ...
    call mvnw.cmd spring-boot:run
)

pause
