-- Greengrocer Database Schema
-- Run this script in MySQL to create the database and tables

-- ============================================
-- CREATE DATABASE USER (myuser@localhost with password 1234)
-- Run these commands as MySQL root user first
-- ============================================
-- Note: If user already exists, you may need to DROP USER first
-- DROP USER IF EXISTS 'myuser'@'localhost';
CREATE USER IF NOT EXISTS 'myuser'@'localhost' IDENTIFIED BY '1234';

-- Create database
CREATE DATABASE IF NOT EXISTS greengrocer;

-- Grant privileges to myuser
GRANT ALL PRIVILEGES ON greengrocer.* TO 'myuser'@'localhost';
FLUSH PRIVILEGES;

USE greengrocer;

-- ============================================
-- USER INFO TABLE
-- Stores all users: customers, carriers, and owner
-- ============================================
CREATE TABLE IF NOT EXISTS UserInfo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('CUSTOMER', 'CARRIER', 'OWNER') NOT NULL,
    full_name VARCHAR(100),
    address VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(100),
    loyalty_points INT DEFAULT 0,
    completed_orders INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- PRODUCT INFO TABLE
-- Stores vegetables and fruits with BLOB for images
-- ============================================
CREATE TABLE IF NOT EXISTS ProductInfo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type ENUM('VEGETABLE', 'FRUIT') NOT NULL,
    price DOUBLE NOT NULL,
    stock DOUBLE NOT NULL,
    threshold DOUBLE DEFAULT 5.0,
    image LONGBLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- ORDER INFO TABLE
-- Stores customer orders with invoice as CLOB and PDF as BLOB
-- ============================================
CREATE TABLE IF NOT EXISTS OrderInfo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    carrier_id INT,
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    requested_delivery DATETIME NOT NULL,
    delivery_time DATETIME,
    status ENUM('PENDING', 'SELECTED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    subtotal DOUBLE NOT NULL,
    vat DOUBLE NOT NULL,
    discount DOUBLE DEFAULT 0,
    total_cost DOUBLE NOT NULL,
    invoice LONGTEXT,
    invoice_pdf LONGBLOB,
    FOREIGN KEY (user_id) REFERENCES UserInfo(id),
    FOREIGN KEY (carrier_id) REFERENCES UserInfo(id)
);

-- ============================================
-- ORDER ITEMS TABLE
-- Stores individual items in each order
-- ============================================
CREATE TABLE IF NOT EXISTS OrderItems (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    quantity DOUBLE NOT NULL,
    price_at_time DOUBLE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES OrderInfo(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES ProductInfo(id)
);

-- ============================================
-- MESSAGES TABLE
-- Customer-Owner communication
-- ============================================
CREATE TABLE IF NOT EXISTS Messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    subject VARCHAR(200),
    content TEXT NOT NULL,
    reply TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (sender_id) REFERENCES UserInfo(id),
    FOREIGN KEY (receiver_id) REFERENCES UserInfo(id)
);

-- ============================================
-- COUPONS TABLE
-- Discount coupons managed by owner
-- ============================================
CREATE TABLE IF NOT EXISTS Coupons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_percent DOUBLE NOT NULL,
    min_order_value DOUBLE DEFAULT 0,
    expiry_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- USER COUPONS TABLE
-- Tracks which coupons are assigned to which users
-- ============================================
CREATE TABLE IF NOT EXISTS UserCoupons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    coupon_id INT NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES UserInfo(id),
    FOREIGN KEY (coupon_id) REFERENCES Coupons(id)
);

-- ============================================
-- RATINGS TABLE
-- Customer ratings for carriers
-- ============================================
CREATE TABLE IF NOT EXISTS Ratings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    carrier_id INT NOT NULL,
    customer_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES OrderInfo(id),
    FOREIGN KEY (carrier_id) REFERENCES UserInfo(id),
    FOREIGN KEY (customer_id) REFERENCES UserInfo(id)
);

-- ============================================
-- LOYALTY SETTINGS TABLE
-- Settings for loyalty discount (managed by owner)
-- ============================================
CREATE TABLE IF NOT EXISTS LoyaltySettings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    min_orders_for_discount INT DEFAULT 5,
    discount_percent DOUBLE DEFAULT 10.0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================
-- INSERT DEFAULT DATA
-- ============================================

-- Insert default users (cust, carr, own)
INSERT INTO UserInfo (username, password, role, full_name, address, phone, email) VALUES
('cust', 'cust', 'CUSTOMER', 'Test Customer', '123 Customer Street', '555-0001', 'customer@test.com'),
('carr', 'carr', 'CARRIER', 'Test Carrier', '456 Carrier Avenue', '555-0002', 'carrier@test.com'),
('own', 'own', 'OWNER', 'Store Owner', '789 Owner Boulevard', '555-0003', 'owner@test.com');

-- Insert 12 Vegetables
INSERT INTO ProductInfo (name, type, price, stock, threshold) VALUES
('Tomato', 'VEGETABLE', 2.50, 100.0, 5.0),
('Potato', 'VEGETABLE', 1.50, 150.0, 5.0),
('Onion', 'VEGETABLE', 1.00, 120.0, 5.0),
('Carrot', 'VEGETABLE', 1.80, 80.0, 5.0),
('Cucumber', 'VEGETABLE', 2.00, 90.0, 5.0),
('Pepper', 'VEGETABLE', 3.00, 70.0, 5.0),
('Broccoli', 'VEGETABLE', 3.50, 50.0, 5.0),
('Spinach', 'VEGETABLE', 2.20, 60.0, 5.0),
('Lettuce', 'VEGETABLE', 1.80, 75.0, 5.0),
('Cabbage', 'VEGETABLE', 1.20, 85.0, 5.0),
('Eggplant', 'VEGETABLE', 2.80, 55.0, 5.0),
('Zucchini', 'VEGETABLE', 2.40, 65.0, 5.0);

-- Insert 12 Fruits
INSERT INTO ProductInfo (name, type, price, stock, threshold) VALUES
('Apple', 'FRUIT', 3.00, 100.0, 5.0),
('Banana', 'FRUIT', 2.00, 120.0, 5.0),
('Orange', 'FRUIT', 2.80, 90.0, 5.0),
('Grape', 'FRUIT', 5.00, 60.0, 5.0),
('Strawberry', 'FRUIT', 6.00, 40.0, 5.0),
('Watermelon', 'FRUIT', 4.00, 30.0, 5.0),
('Mango', 'FRUIT', 4.50, 50.0, 5.0),
('Pineapple', 'FRUIT', 3.50, 45.0, 5.0),
('Peach', 'FRUIT', 3.80, 55.0, 5.0),
('Pear', 'FRUIT', 3.20, 65.0, 5.0),
('Cherry', 'FRUIT', 7.00, 35.0, 5.0),
('Kiwi', 'FRUIT', 4.20, 48.0, 5.0);

-- Insert default loyalty settings
INSERT INTO LoyaltySettings (min_orders_for_discount, discount_percent) VALUES (5, 10.0);

-- Insert a sample coupon
INSERT INTO Coupons (code, discount_percent, min_order_value, expiry_date, is_active) VALUES
('WELCOME10', 10.0, 20.0, '2027-12-31', TRUE),
('SAVE20', 20.0, 50.0, '2027-12-31', TRUE);
