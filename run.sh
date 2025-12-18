#!/bin/bash
# Group17 GreenGrocer - Run Script
# ============================================

# Get the script's directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Change to the script's directory
cd "$SCRIPT_DIR"

# Set path to JavaFX SDK lib folder
PATH_TO_FX="$SCRIPT_DIR/lib/javafx-sdk-21.0.9/lib"

# Set path to MySQL Connector JAR
MYSQL_JAR="$SCRIPT_DIR/lib/mysql-connector-j-8.0.33.jar"

# Output directory
OUT_DIR="$SCRIPT_DIR/out"

echo ""
echo "========================================"
echo "   Group17 GreenGrocer - Starting..."
echo "========================================"
echo ""

# Check if output exists
if [ ! -d "$OUT_DIR" ]; then
    echo "ERROR: Output directory not found. Please run build.sh first."
    exit 1
fi

echo "Starting application..."
java --module-path "$PATH_TO_FX" \
     --add-modules javafx.controls,javafx.fxml,javafx.graphics \
     --enable-native-access=javafx.graphics \
     -cp "$OUT_DIR:$MYSQL_JAR" \
     com.greengrocer.Main

if [ $? -ne 0 ]; then
    echo ""
    echo "Application exited with error."
fi
