-- EarnSafe Database Schema
-- Run this script to create the database and tables

CREATE DATABASE IF NOT EXISTS earnsafe_db;
USE earnsafe_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    city VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Policies table
CREATE TABLE IF NOT EXISTS policies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    premium_amount DOUBLE NOT NULL,
    coverage_amount DOUBLE NOT NULL,
    risk_score DOUBLE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    start_date DATETIME,
    end_date DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_status (user_id, status),
    INDEX idx_status (status)
);

-- Claims table
CREATE TABLE IF NOT EXISTS claims (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    disruption_type VARCHAR(30),
    disruption_details TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    claim_amount DOUBLE,
    fraud_score DOUBLE,
    is_fraudulent BOOLEAN DEFAULT FALSE,
    fraud_reason TEXT,
    weather_temp DOUBLE,
    weather_humidity DOUBLE,
    weather_rainfall DOUBLE,
    weather_wind_speed DOUBLE,
    triggered_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (policy_id) REFERENCES policies(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_created (created_at)
);

-- Weather data table
CREATE TABLE IF NOT EXISTS weather_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    latitude DOUBLE,
    longitude DOUBLE,
    city VARCHAR(100),
    temperature DOUBLE,
    feels_like DOUBLE,
    humidity DOUBLE,
    pressure DOUBLE,
    wind_speed DOUBLE,
    rainfall DOUBLE,
    visibility DOUBLE,
    aqi INT,
    weather_main VARCHAR(50),
    weather_description VARCHAR(255),
    fetched_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_location (latitude, longitude),
    INDEX idx_fetched (fetched_at)
);

-- Payouts table
CREATE TABLE IF NOT EXISTS payouts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DOUBLE,
    razorpay_payout_id VARCHAR(255),
    razorpay_status VARCHAR(50),
    transaction_details TEXT,
    processed_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (claim_id) REFERENCES claims(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user (user_id),
    INDEX idx_claim (claim_id)
);
