# Group17 GreenGrocer - JavaFX Desktop Application

A complete desktop application for a Local Greengrocer built with JavaFX and JDBC.

## Project Structure

```
Project3/
├── sql/
│   └── Group17.sql             # Database schema and initial data
├── src/
│   └── com/greengrocer/
│       ├── Main.java           # Application entry point
│       ├── controllers/        # FXML Controllers
│       ├── database/           # Database adapter and DAOs
│       ├── models/             # Data models
│       ├── utils/              # Utility classes
│       ├── views/              # FXML files
│       └── styles/             # CSS stylesheets
├── lib/                        # Required JAR libraries (NOT tracked in git)
│   ├── javafx-sdk-25.0.1/     # JavaFX SDK - download for your OS
│   ├── mysql-connector-j-8.0.33.jar  # MySQL JDBC driver
│   └── itextpdf-5.5.13.3.jar  # PDF generation library
└── README.md
```

## ⚠️ Important: Library Dependencies

**The `lib/` directory is NOT tracked in git** because it contains platform-specific dependencies. Each developer must download the required libraries for their own operating system.

### Required Libraries

1. **JavaFX SDK** (Platform-specific - Windows/Linux/macOS)
2. **MySQL Connector/J** (Platform-independent)
3. **iTextPDF** (Platform-independent)

See the detailed setup instructions below.
```

## Prerequisites

1. **Java JDK 21 or higher** (JDK 23 recommended for JavaFX 25)
   - Check your version: `java -version`
   - Download JDK: https://adoptium.net/temurin/releases/

   > **⚠️ IMPORTANT**: 
   > - JavaFX SDK is **platform-specific** - download the correct version for your OS
   > - If you get "class file has wrong version" errors, your Java and JavaFX versions don't match
   > - Windows SDK contains `.dll` files, Linux has `.so` files, macOS has `.dylib` files

3. **MySQL Server 8.0+** (or MariaDB)

4. **MySQL Connector/J 8.0.33** (Platform-independent JAR)
   - Download: https://dev.mysql.com/downloads/connector/j/
   - Or direct: https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar

5. **iTextPDF 5.5.13.3** (Platform-independent JAR for PDF invoice generation)
   - Download: https://repo1.maven.org/maven2/com/itextpdf/itextpdf/5.5.13.3/itextpdf-5.5.13.3.jar

   | Your Java Version | Required JavaFX SDK | Download Link |
   |-------------------|---------------------|---------------|
   | Java 23 | JavaFX 25.0.1 | [Windows](https://download2.gluonhq.com/openjfx/25.0.1/openjfx-25.0.1_windows-x64_bin-sdk.zip) / [Linux](https://download2.gluonhq.com/openjfx/25.0.1/openjfx-25.0.1_linux-x64_bin-sdk.zip) / [macOS](https://download2.gluonhq.com/openjfx/25.0.1/openjfx-25.0.1_osx-aarch64_bin-sdk.zip) |
   | Java 21 | JavaFX 21.0.5 | [Windows](https://download2.gluonhq.com/openjfx/21.0.5/openjfx-21.0.5_windows-x64_bin-sdk.zip) / [Linux](https://download2.gluonhq.com/openjfx/21.0.5/openjfx-21.0.5_linux-x64_bin-sdk.zip) / [macOS](https://download2.gluonhq.com/openjfx/21.0.5/openjfx-21.0.5_osx-aarch64_bin-sdk.zip) |
   | Java 17 | JavaFX 21.0.5 | Same as above |

   > **⚠️ IMPORTANT**: If you get "class file has wrong version" errors, your Java and JavaFX versions don't match!

3. **MySQL Server 8.0+** (or MariaDB)
4. **MySQL Connector/J 8.0.33** - Already included in `lib/mysql-connector-j-8.0.33.jar`
5. **iTextPDF 5.5.13.3** - Already included in `lib/itextpdf-5.5.13.3.jar` (for PDF invoice generation)

## Setup Instructions

### 1. Create lib Directory and Download Libraries

Since the `lib/` directory is not tracked in git (platform-specific dependencies), you need to create it and download all required libraries:

#### Step 1: Create the lib directory
```bash
mkdir -p lib
cd lib
```

#### Step 2: Download JavaFX SDK (Platform-specific)

**Choose your operating system:**

**Linux (x64):**
```bash
wget https://download2.gluonhq.com/openjfx/25.0.1/openjfx-25.0.1_linux-x64_bin-sdk.zip
unzip openjfx-25.0.1_linux-x64_bin-sdk.zip
rm openjfx-25.0.1_linux-x64_bin-sdk.zip
```

**macOS (ARM - Apple Silicon):**
```bash
wget https://download2.gluonhq.com/openjfx/25.0.1/openjfx-25.0.1_osx-aarch64_bin-sdk.zip
unzip openjfx-25.0.1_osx-aarch64_bin-sdk.zip
rm openjfx-25.0.1_osx-aarch64_bin-sdk.zip
```

**macOS (Intel):**
```bash
wget https://download2.gluonhq.com/openjfx/25.0.1/openjfx-25.0.1_osx-x64_bin-sdk.zip
unzip openjfx-25.0.1_osx-x64_bin-sdk.zip
rm openjfx-25.0.1_osx-x64_bin-sdk.zip
```

**Windows:**
1. Download: https://download2.gluonhq.com/openjfx/25.0.1/openjfx-25.0.1_windows-x64_bin-sdk.zip
2. Extract to `lib/` directory
3. Ensure the folder is named `javafx-sdk-25.0.1`

#### Step 3: Download MySQL Connector/J
```bash
wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
```

Or download manually from: https://dev.mysql.com/downloads/connector/j/

#### Step 4: Download iTextPDF
```bash
wget https://repo1.maven.org/maven2/com/itextpdf/itextpdf/5.5.13.3/itextpdf-5.5.13.3.jar
```

#### Step 5: Verify Directory Structure

Your `lib/` directory should now look like this:
```
lib/
├── javafx-sdk-25.0.1/
│   └── lib/
│       ├── javafx.base.jar
│       ├── javafx.controls.jar
│       ├── javafx.fxml.jar
│       ├── javafx.graphics.jar
│       └── libglassgtk3.so (Linux) / glass.dll (Windows) / libglass.dylib (macOS)
├── mysql-connector-j-8.0.33.jar
└── itextpdf-5.5.13.3.jar
```

> **⚠️ CRITICAL**: 
> - The JavaFX SDK **must** match your operating system
> - Linux SDK contains `.so` files, Windows has `.dll`, macOS has `.dylib`
> - Using the wrong platform's SDK will cause `ClassNotFoundException: GtkPlatformFactory` or similar errors
> - The `lib/` directory is in `.gitignore` - **do not commit these files to git**

### 2. VS Code Configuration (Optional)

The `.vscode/` directory is also excluded from git as it contains user-specific IDE settings. If you're using VS Code, you may want to create your own configuration:

**Example `.vscode/settings.json`:**
```json
{
    "java.project.sourcePaths": ["src"],
    "java.project.outputPath": "out",
    "java.project.referencedLibraries": [
        "lib/**/*.jar"
    ]
}
```

### 3. Database Setup

1. Start MySQL Server
2. Login to MySQL:
   ```
   mysql -u root -p
   ```
3. Create the application user:
   ```sql
   CREATE USER 'myuser'@'localhost' IDENTIFIED BY '1234';
   GRANT ALL PRIVILEGES ON greengrocer.* TO 'myuser'@'localhost';
   FLUSH PRIVILEGES;
   ```
4. Run the schema script:
The SQL file automatically creates the database, user, and all tables. Just run it as the MySQL **root** user:

**Option 1: Command Line**
```bash
   # MySQL
   mysql -u myuser -p1234 < sql/Group17.sql
   
   # Or MariaDB
   mariadb -u myuser -p1234 < sql/Group17.sql
   ```

   # Run as root user (the SQL file creates 'myuser' automatically)
   mysql -u root -p < sql/Group17.sql
   ```

   **Option 2: MySQL Workbench / phpMyAdmin**
   1. Connect as root user
   2. Open and execute `sql/Group17.sql`

   > **Note**: The script creates user `myuser` with password `1234`. If you get "Access denied" errors when running the app, make sure you ran the SQL file as **root**, not as myuser.

   ## Compilation Instructions

   ### Using Build Scripts (Recommended)

   **Linux/macOS:**
   ```bash
   ./build.sh
   ./run.sh
   
   ```

**Windows:**
```cmd
build.bat
run.bat
```

The build and run scripts are pre-configured to use the JavaFX SDK from `lib/javafx-sdk-25.0.1/` and the MySQL connector.

### Manual Compilation (Alternative)

If you prefer to compile manually or need to customize paths:

**Step 1: Set Environment Variables**

Set the path to your JavaFX SDK:

**Windows (PowerShell):**
```powershell
$env:PATH_TO_FX = "lib\javafx-sdk-25.0.1\lib"
$env:MYSQL_JAR = "lib\mysql-connector-j-8.0.33.jar"
```

**Windows (Command Prompt):**
```cmd
set PATH_TO_FX=lib\javafx-sdk-25.0.1\lib
set MYSQL_JAR=lib\mysql-connector-j-8.0.33.jar
```

**Linux/macOS (Bash):**
```bash
export PATH_TO_FX="lib/javafx-sdk-25.0.1/lib"
export MYSQL_JAR="lib/mysql-connector-j-8.0.33.jar"
```

### Step 2: Compile the Source Files

From the project root directory:

**Windows (PowerShell):**
```powershell
# Create output directory
New-Item -ItemType Directory -Force -Path "out"

# Compile all Java files
javac --module-path $env:PATH_TO_FX --add-modules javafx.controls,javafx.fxml `
    -cp "$env:MYSQL_JAR" `
    -d out `
    (Get-ChildItem -Path "src" -Filter "*.java" -Recurse).FullName

# Copy resources
Copy-Item -Path "src\com\greengrocer\views" -Destination "out\com\greengrocer\" -Recurse -Force
Copy-Item -Path "src\com\greengrocer\styles" -Destination "out\com\greengrocer\" -Recurse -Force
```

**Windows (Command Prompt):**
```cmd
:: Create output directory
mkdir out

:: Compile all Java files
dir /s /B src\*.java > sources.txt
javac --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -cp %MYSQL_JAR% -d out @sources.txt

:: Copy resources
xcopy /s /i src\com\greengrocer\views out\com\greengrocer\views
xcopy /s /i src\com\greengrocer\styles out\com\greengrocer\styles
```

### Step 3: Run the Application

**Windows (PowerShell):**
```powershell
java --module-path $env:PATH_TO_FX --add-modules javafx.controls,javafx.fxml `
    -cp "out;$env:MYSQL_JAR" `
    com.greengrocer.Main
```

**Windows (Command Prompt):**
```cmd
java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -cp "out;%MYSQL_JAR%" com.greengrocer.Main
```

## Default Users

| Username | Password | Role     |
|----------|----------|----------|
| cust     | cust     | Customer |
| carr     | carr     | Carrier  |
| own      | own      | Owner    |

## Features

### Customer Interface
- Browse vegetables and fruits in expandable sections
- Search products by name
- Add products to cart with quantity validation
- View threshold-based pricing (doubled when stock is low)
- Shopping cart with item merging
- Apply loyalty discounts (earned after X orders)
- Select delivery date/time (within 48 hours)
- Minimum cart value requirement
- View order history
- Cancel pending orders
- Rate carriers after delivery
- Send messages to owner
- Edit profile information

### Carrier Interface
- View available orders
- Select multiple orders for delivery
- Concurrent selection protection
- Complete deliveries
- View delivery history
- See personal rating

### Owner Interface
- **Products**: Add/edit/delete products with price, stock, and threshold
- **Carriers**: Employ/fire carriers
- **Orders**: View all orders with status filter
- **Messages**: View and reply to customer messages
- **Coupons**: Create coupons, assign to customers
- **Loyalty**: Set loyalty discount settings
- **Ratings**: View all carrier ratings
- **Reports**: Charts for sales by product

## Event Handlers (6+ Required)

1. Button Click - Login, Add to Cart, Checkout, etc.
2. TextField Change - Real-time product search
3. ListView Selection - Order/message selection
4. ComboBox Selection - Order status filter
5. DatePicker Selection - Delivery date selection
6. TableView Selection - Product/carrier selection
7. Window Close - Logout confirmation

## Generating JavaDoc

```powershell
javadoc -d docs --module-path $env:PATH_TO_FX --add-modules javafx.controls,javafx.fxml `
    -cp $env:MYSQL_JAR `
    -subpackages com.greengrocer `
    -sourcepath src
```

## Troubleshooting

1. **ClassNotFoundException: com.sun.glass.ui.gtk.GtkPlatformFactory** (Linux):
   - You're using the Windows version of JavaFX SDK instead of the Linux version
   - Delete `lib/javafx-sdk-25.0.1/` and download the correct Linux SDK
   - Verify the SDK contains `.so` files, not `.dll` files

2. **Graphics Device initialization failed / No suitable pipeline found**:
   - You're using the wrong JavaFX SDK for your operating system
   - Download the correct platform-specific SDK (Windows/Linux/macOS)
   - Ensure the SDK is extracted to `lib/javafx-sdk-25.0.1/`
   - Linux: May need to install GTK3 libraries: `sudo apt install libgtk-3-0 libgl1`
   - Wayland users: Set environment variable `_JAVA_AWT_WM_NONREPARENTING=1`

3. **FileNotFoundException or lib/ directory missing**:
   - The `lib/` directory is not tracked in git - you must create it and download all libraries
   - Follow the "Setup Instructions" section above
   - Ensure all three libraries are present: JavaFX SDK, MySQL Connector, iTextPDF

4. **Database Connection Error**:
   - Ensure MySQL/MariaDB is running: `systemctl status mysql` or `systemctl status mariadb`
   - Verify user credentials are correct (myuser/1234)
   - Check if user has permissions: `GRANT ALL PRIVILEGES ON greengrocer.* TO 'myuser'@'localhost';`

5. **ClassNotFoundException (MySQL/iText related)**:
   - Verify MySQL Connector JAR is in `lib/` directory
   - Check build script is including it in classpath

6. **FXML Load Error**:
   - Ensure resources are copied to output directory
   - Check that `src/com/greengrocer/views/` and `styles/` exist

7. **JavaFX Module Error**:
   - Verify JavaFX SDK path is correct in build/run scripts
   - Ensure you're using Java 11 or later

## Authors

Group17 - CMPE 343 Object Oriented Programming

## License

University Project - Educational Use Only
