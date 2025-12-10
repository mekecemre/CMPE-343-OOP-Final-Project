@echo off
REM Group17 GreenGrocer - Run Script
REM ============================================

REM Change to the script's directory
cd /d "%~dp0"

REM Set path to JavaFX SDK lib folder
set "PATH_TO_FX=%~dp0lib\javafx-sdk-21.0.9\lib"

REM Set path to MySQL Connector JAR
set "MYSQL_JAR=%~dp0lib\mysql-connector-j-8.0.33.jar"

REM Output directory
set "OUT_DIR=%~dp0out"

echo.
echo ========================================
echo    Group17 GreenGrocer - Starting...
echo ========================================
echo.

REM Check if output exists
if not exist "%OUT_DIR%" (
    echo ERROR: Output directory not found. Please run build.bat first.
    pause
    exit /b 1
)

echo Starting application...
java --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml,javafx.graphics --enable-native-access=javafx.graphics -cp "%OUT_DIR%;%MYSQL_JAR%" com.greengrocer.Main

if errorlevel 1 (
    echo.
    echo Application exited with error.
    pause
)
