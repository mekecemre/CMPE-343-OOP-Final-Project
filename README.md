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
├── lib/                        # Required JAR libraries
│   ├── javafx-sdk-21.0.9/     # JavaFX SDK (download for your OS, not tracked in git)
│   └── mysql-connector-j-8.0.33.jar
└── README.md
```

## Prerequisites

1. **Java JDK 21** (or compatible version with JavaFX)
2. **JavaFX SDK 21** - Download the platform-specific SDK:
   - **Windows**: [JavaFX 21.0.1 Windows SDK](https://download2.gluonhq.com/openjfx/21.0.1/openjfx-21.0.1_windows-x64_bin-sdk.zip)
   - **Linux**: [JavaFX 21.0.1 Linux SDK](https://download2.gluonhq.com/openjfx/21.0.1/openjfx-21.0.1_linux-x64_bin-sdk.zip)
   - **macOS**: [JavaFX 21.0.1 macOS SDK](https://download2.gluonhq.com/openjfx/21.0.1/openjfx-21.0.1_osx-x64_bin-sdk.zip)
   - Or download from: https://openjfx.io/
3. **MySQL Server 8.0+** (or MariaDB)
4. **MySQL Connector/J 8.0.33** - Already included in `lib/mysql-connector-j-8.0.33.jar`

## Setup Instructions

### 1. JavaFX SDK Setup

Download the JavaFX SDK for your operating system (see Prerequisites) and extract it to the `lib/` directory:

**Example structure after extraction:**
```
lib/
├── javafx-sdk-21.0.9/
│   └── lib/
│       ├── javafx.base.jar
│       ├── javafx.controls.jar
│       ├── javafx.fxml.jar
│       └── ... (platform-specific native libraries)
└── mysql-connector-j-8.0.33.jar
```

**Note:** The JavaFX SDK is platform-specific (contains `.dll` for Windows, `.so` for Linux, `.dylib` for macOS). Each developer must download the SDK for their own operating system. The SDK is not tracked in git.

### 2. Database Setup

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
   ```bash
   # MySQL
   mysql -u myuser -p1234 < sql/Group17.sql
   
   # Or MariaDB
   mariadb -u myuser -p1234 < sql/Group17.sql
   ```

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

The build and run scripts are pre-configured to use the JavaFX SDK from `lib/javafx-sdk-21.0.9/` and the MySQL connector.

### Manual Compilation (Alternative)

If you prefer to compile manually or need to customize paths:

**Step 1: Set Environment Variables**

Set the path to your JavaFX SDK:

**Windows (PowerShell):**
```powershell
$env:PATH_TO_FX = "lib\javafx-sdk-21.0.9\lib"
$env:MYSQL_JAR = "lib\mysql-connector-j-8.0.33.jar"
```

**Windows (Command Prompt):**
```cmd
set PATH_TO_FX=lib\javafx-sdk-21.0.9\lib
set MYSQL_JAR=lib\mysql-connector-j-8.0.33.jar
```

**Linux/macOS (Bash):**
```bash
export PATH_TO_FX="lib/javafx-sdk-21.0.9/lib"
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

1. **Graphics Device initialization failed / No suitable pipeline found**:
   - You're using the wrong JavaFX SDK for your operating system
   - Download the correct platform-specific SDK (Windows/Linux/macOS)
   - Ensure the SDK is extracted to `lib/javafx-sdk-21.0.9/`
   - Linux: May need to install `libgtk-3-0` and OpenGL libraries

2. **Database Connection Error**: 
   - Ensure MySQL/MariaDB is running: `systemctl status mysql` or `systemctl status mariadb`
   - Verify user credentials are correct (myuser/1234)
   - Check if user has permissions: `GRANT ALL PRIVILEGES ON greengrocer.* TO 'myuser'@'localhost';`

3. **ClassNotFoundException**: 
   - Verify MySQL Connector JAR is in `lib/` directory
   - Check build script is including it in classpath

4. **FXML Load Error**: 
   - Ensure resources are copied to output directory
   - Check that `src/com/greengrocer/views/` and `styles/` exist

5. **JavaFX Module Error**: 
   - Verify JavaFX SDK path is correct in build/run scripts
   - Ensure you're using Java 11 or later

## Authors

Group17 - CMPE 343 Object Oriented Programming

## License

University Project - Educational Use Only
