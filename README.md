# Group17 GreenGrocer - JavaFX Desktop Application

A complete desktop application for a Local Greengrocer built with JavaFX and JDBC.

## Project Structure

```
Project3/
├── sql/
│   └── schema.sql              # Database schema and initial data
├── src/
│   └── com/greengrocer/
│       ├── Main.java           # Application entry point
│       ├── controllers/        # FXML Controllers
│       ├── database/           # Database adapter and DAOs
│       ├── models/             # Data models
│       ├── utils/              # Utility classes
│       ├── views/              # FXML files
│       └── styles/             # CSS stylesheets
├── lib/                        # Required JAR libraries (add these)
│   ├── javafx-sdk-21/         # JavaFX SDK
│   └── mysql-connector-j-8.0.33.jar
└── README.md
```

## Prerequisites

1. **Java JDK 21** (or compatible version with JavaFX)
2. **JavaFX SDK 21** - Download from https://openjfx.io/
3. **MySQL Server 8.0+**
4. **MySQL Connector/J** - Download from https://dev.mysql.com/downloads/connector/j/

## Database Setup

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
   ```
   mysql -u myuser -p1234 < sql/schema.sql
   ```

## Compilation Instructions

### Step 1: Set Environment Variables

Set the path to your JavaFX SDK:

**Windows (PowerShell):**
```powershell
$env:PATH_TO_FX = "C:\path\to\javafx-sdk-21\lib"
$env:MYSQL_JAR = "C:\path\to\mysql-connector-j-8.0.33.jar"
```

**Windows (Command Prompt):**
```cmd
set PATH_TO_FX=C:\path\to\javafx-sdk-21\lib
set MYSQL_JAR=C:\path\to\mysql-connector-j-8.0.33.jar
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

1. **Database Connection Error**: Ensure MySQL is running and user credentials are correct
2. **ClassNotFoundException**: Verify MySQL Connector JAR is in classpath
3. **FXML Load Error**: Ensure resources are copied to output directory
4. **JavaFX Error**: Verify JavaFX SDK path is correct

## Authors

Group17 - CMPE 343 Object Oriented Programming

## License

University Project - Educational Use Only
