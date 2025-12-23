@echo off
REM Group17 GreenGrocer - Run Script
REM ============================================

REM Change to the script's directory
cd /d "%~dp0"

REM ============================================
REM AUTO-DETECT NEWEST JAVA VERSION
REM ============================================
set "JAVA_HOME_FOUND="
set "JAVA_CMD=java"

REM Check common JDK installation paths for newest version (check newest first)
for %%V in (23 22 21) do (
    if not defined JAVA_HOME_FOUND (
        REM Check Program Files
        if exist "C:\Program Files\Java\jdk-%%V\bin\java.exe" (
            set "JAVA_HOME_FOUND=C:\Program Files\Java\jdk-%%V"
            set "JAVA_CMD=C:\Program Files\Java\jdk-%%V\bin\java.exe"
        )
        REM Check Eclipse Adoptium/Temurin
        if exist "C:\Program Files\Eclipse Adoptium\jdk-%%V*\bin\java.exe" (
            for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-%%V*") do (
                set "JAVA_HOME_FOUND=%%D"
                set "JAVA_CMD=%%D\bin\java.exe"
            )
        )
        REM Check Amazon Corretto
        if exist "C:\Program Files\Amazon Corretto\jdk%%V*\bin\java.exe" (
            for /d %%D in ("C:\Program Files\Amazon Corretto\jdk%%V*") do (
                set "JAVA_HOME_FOUND=%%D"
                set "JAVA_CMD=%%D\bin\java.exe"
            )
        )
    )
)

REM If JAVA_HOME is set by user, prefer that
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_HOME_FOUND=%JAVA_HOME%"
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
    )
)

REM Set path to JavaFX SDK lib folder
set "PATH_TO_FX=%~dp0lib\javafx-sdk-25.0.1\lib"

REM Set path to MySQL Connector JAR
set "MYSQL_JAR=%~dp0lib\mysql-connector-j-8.0.33.jar"

REM Set path to iTextPDF JAR
set "ITEXT_JAR=%~dp0lib\itextpdf-5.5.13.3.jar"

REM Output directory
set "OUT_DIR=%~dp0out"

echo.
echo ========================================
echo    Group17 GreenGrocer - Starting...
echo ========================================
echo.

REM Show which Java is being used
if defined JAVA_HOME_FOUND (
    echo Using Java from: %JAVA_HOME_FOUND%
) else (
    echo Using Java from system PATH
)
echo.

REM Check if output exists
if not exist "%OUT_DIR%" (
    echo ERROR: Output directory not found. Please run build.bat first.
    pause
    exit /b 1
)

echo Starting application...
"%JAVA_CMD%" --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml,javafx.graphics --enable-native-access=javafx.graphics -cp "%OUT_DIR%;%MYSQL_JAR%;%ITEXT_JAR%" com.greengrocer.Main

if errorlevel 1 (
    echo.
    echo Application exited with error.
    pause
)
