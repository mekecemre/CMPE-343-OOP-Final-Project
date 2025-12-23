@echo off
REM Group17 GreenGrocer - Build and Run Script
REM ============================================
REM Paths are relative to the script location

REM Change to the script's directory
cd /d "%~dp0"

REM Set path to JavaFX SDK lib folder
set "PATH_TO_FX=%~dp0lib\javafx-sdk-25.0.1\lib"

REM Set path to MySQL Connector JAR
set "MYSQL_JAR=%~dp0lib\mysql-connector-j-8.0.33.jar"

REM Set path to iTextPDF JAR
set "ITEXT_JAR=%~dp0lib\itextpdf-5.5.13.3.jar"

REM Project directories
set "SRC_DIR=%~dp0src"
set "OUT_DIR=%~dp0out"

echo.
echo ========================================
echo    Group17 GreenGrocer - Build Script
echo ========================================
echo.

REM Check if JavaFX path exists
if not exist "%PATH_TO_FX%" (
    echo ERROR: JavaFX SDK not found at %PATH_TO_FX%
    echo Please update PATH_TO_FX in this script.
    pause
    exit /b 1
)

REM Check if MySQL connector exists
if not exist "%MYSQL_JAR%" (
    echo ERROR: MySQL Connector not found at %MYSQL_JAR%
    echo Please update MYSQL_JAR in this script.
    pause
    exit /b 1
)

echo [1/3] Creating output directory...
if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
mkdir "%OUT_DIR%"

echo [2/3] Compiling Java sources...
REM Use forfiles to handle spaces in paths correctly
setlocal enabledelayedexpansion
set "JAVA_FILES="
for /r "%SRC_DIR%" %%f in (*.java) do (
    set "JAVA_FILES=!JAVA_FILES! "%%f""
)

javac -encoding UTF-8 --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "%MYSQL_JAR%;%ITEXT_JAR%" -d "%OUT_DIR%" %JAVA_FILES%
endlocal

if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo [3/3] Copying resources...
xcopy /s /i /q "%SRC_DIR%\com\greengrocer\views" "%OUT_DIR%\com\greengrocer\views"
xcopy /s /i /q "%SRC_DIR%\com\greengrocer\styles" "%OUT_DIR%\com\greengrocer\styles"

echo.
echo ========================================
echo    Build Successful!
echo ========================================
echo.
echo To run the application, use: run.bat
echo.
pause
