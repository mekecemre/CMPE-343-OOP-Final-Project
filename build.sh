#!/bin/bash
# Group17 GreenGrocer - Build and Run Script
# ============================================
# Paths are relative to the script location

# Get the script's directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Change to the script's directory
cd "$SCRIPT_DIR"

# Set path to JavaFX SDK lib folder
PATH_TO_FX="$SCRIPT_DIR/lib/javafx-sdk-25.0.1/lib"

# Set path to MySQL Connector JAR
MYSQL_JAR="$SCRIPT_DIR/lib/mysql-connector-j-8.0.33.jar"

# Set path to iTextPDF JAR
ITEXT_JAR="$SCRIPT_DIR/lib/itextpdf-5.5.13.3.jar"

# Project directories
SRC_DIR="$SCRIPT_DIR/src"
OUT_DIR="$SCRIPT_DIR/out"

echo ""
echo "========================================"
echo "   Group17 GreenGrocer - Build Script"
echo "========================================"
echo ""

# Check if JavaFX path exists
if [ ! -d "$PATH_TO_FX" ]; then
    echo "ERROR: JavaFX SDK not found at $PATH_TO_FX"
    echo "Please update PATH_TO_FX in this script."
    exit 1
fi

# Check if MySQL connector exists
if [ ! -f "$MYSQL_JAR" ]; then
    echo "ERROR: MySQL Connector not found at $MYSQL_JAR"
    echo "Please update MYSQL_JAR in this script."
    exit 1
fi

echo "[1/3] Creating output directory..."
if [ -d "$OUT_DIR" ]; then
    rm -rf "$OUT_DIR"
fi
mkdir -p "$OUT_DIR"

echo "[2/3] Compiling Java sources..."
# Find all Java files and compile them
find "$SRC_DIR" -name "*.java" > /tmp/sources.txt

javac --module-path "$PATH_TO_FX" \
      --add-modules javafx.controls,javafx.fxml,javafx.graphics \
      -cp "$MYSQL_JAR:$ITEXT_JAR" \
      -d "$OUT_DIR" \
      @/tmp/sources.txt

if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: Compilation failed!"
    rm -f /tmp/sources.txt
    exit 1
fi

rm -f /tmp/sources.txt

echo "[3/3] Copying resources..."
if [ -d "$SRC_DIR/com/greengrocer/views" ]; then
    mkdir -p "$OUT_DIR/com/greengrocer/views"
    cp -r "$SRC_DIR/com/greengrocer/views/"* "$OUT_DIR/com/greengrocer/views/" 2>/dev/null || true
fi

if [ -d "$SRC_DIR/com/greengrocer/styles" ]; then
    mkdir -p "$OUT_DIR/com/greengrocer/styles"
    cp -r "$SRC_DIR/com/greengrocer/styles/"* "$OUT_DIR/com/greengrocer/styles/" 2>/dev/null || true
fi

echo ""
echo "========================================"
echo "   Build Successful!"
echo "========================================"
echo ""
echo "To run the application, use: ./run.sh"
echo ""
