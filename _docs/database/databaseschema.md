---
title: "Database Schema"
permalink: /docs/database/databaseschema/
excerpt: "Complete Database Schema (SQL)"
last_modified_at: 2026-01-21
toc: true
---

## Complete Schema Reference

```sql
SET SQL_SAFE_UPDATES = 0;

CREATE DATABASE IF NOT EXISTS devovs;

USE devovs;

-- ============================================
-- ONLINE VOTING SYSTEM - FINAL SCHEMA
-- PG-DAC Project - Group 06
-- ============================================

-- 1. Create users table first (no dependencies)
-- Users can be both VOTERS and CANDIDATES
-- Modified to include specific Aadhar and Voter ID columns as required
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    date_of_birth DATE NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(10),
    aadhar_number VARCHAR(12) UNIQUE NOT NULL,
    voter_id_number VARCHAR(20) UNIQUE NOT NULL,
    pan_card_number VARCHAR(255),
    passport_number VARCHAR(255),
    profile_image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    approved_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_aadhar (aadhar_number),
    INDEX idx_voter (voter_id_number)
);

-- 2. Create admins table (separate admin accounts)
-- Admins have their own independent accounts
CREATE TABLE admins (
    admin_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_admin_email (email)
);

-- 3. Create elections table
CREATE TABLE elections (
    election_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    election_name VARCHAR(255) NOT NULL,
    election_type ENUM('GENERAL', 'STATE', 'LOCAL', 'SPECIAL') NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    city VARCHAR(100),
    state VARCHAR(100),
    status ENUM('DRAFT', 'SCHEDULED', 'ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'DRAFT',
    result_published BOOLEAN DEFAULT FALSE,
    result_published_at TIMESTAMP NULL,
    result_published_by BIGINT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES admins(admin_id) ON DELETE RESTRICT,
    FOREIGN KEY (result_published_by) REFERENCES admins(admin_id) ON DELETE SET NULL
);

-- 4. Create candidates table
-- Candidates are users standing for elections
-- Candidate data is hardcoded/predefined by admins
CREATE TABLE candidates (
    candidate_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    election_id BIGINT NOT NULL,
    party_name VARCHAR(255) NOT NULL,
    party_symbol VARCHAR(255) NOT NULL,
    candidate_photo LONGBLOB,
    manifesto TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (election_id) REFERENCES elections(election_id) ON DELETE CASCADE,
    UNIQUE KEY unique_candidate_per_election (user_id, election_id),
    INDEX idx_election (election_id)
);

-- 5. Create votes table
-- Each user can vote only once per election
CREATE TABLE votes (
    vote_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    election_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    vote_hash VARCHAR(255) UNIQUE NOT NULL,
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (election_id) REFERENCES elections(election_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id) ON DELETE RESTRICT,
    UNIQUE KEY unique_vote_per_election (election_id, user_id),
    INDEX idx_election_votes (election_id)
);

-- 6. Create voter_election_status table
-- Track which users have voted in which elections
CREATE TABLE voter_election_status (
    status_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    election_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    has_voted BOOLEAN DEFAULT FALSE,
    voted_at TIMESTAMP NULL,
    FOREIGN KEY (election_id) REFERENCES elections(election_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_voter_election (election_id, user_id)
);

-- 7. Create election_results table
-- Admin generates and displays results here
CREATE TABLE election_results (
    result_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    election_id BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    vote_count BIGINT DEFAULT 0,
    vote_percentage DECIMAL(5,2),
    rank_position INT,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (election_id) REFERENCES elections(election_id) ON DELETE CASCADE,
    FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id) ON DELETE RESTRICT,
    UNIQUE KEY unique_election_candidate (election_id, candidate_id)
);

-- 8. Create election_reports table
-- Admin generates comprehensive election reports
CREATE TABLE election_reports (
    report_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    election_id BIGINT NOT NULL,
    total_registered_voters BIGINT DEFAULT 0,
    total_votes_cast BIGINT DEFAULT 0,
    voter_turnout_percentage DECIMAL(5,2),
    total_candidates INT DEFAULT 0,
    winning_candidate_id BIGINT NULL,
    winning_margin BIGINT NULL,
    report_generated_by BIGINT NOT NULL,
    report_generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (election_id) REFERENCES elections(election_id) ON DELETE CASCADE,
    FOREIGN KEY (winning_candidate_id) REFERENCES candidates(candidate_id) ON DELETE SET NULL,
    FOREIGN KEY (report_generated_by) REFERENCES admins(admin_id) ON DELETE RESTRICT
);

-- 9. Create dummy verification tables
-- Primary verification: AADHAR and VOTER_ID (required)
CREATE TABLE dummy_aadhar_records (
    aadhar_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    aadhar_number VARCHAR(12) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    address TEXT,
    is_valid BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_aadhar (aadhar_number)
);

CREATE TABLE dummy_voter_id_records (
    voter_record_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    voter_id_number VARCHAR(20) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    is_valid BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_voter_id (voter_id_number)
);

-- Secondary verification: PAN and PASSPORT (optional)
CREATE TABLE dummy_pan_records (
    pan_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pan_number VARCHAR(10) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    is_valid BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_pan (pan_number)
);

CREATE TABLE dummy_passport_records (
    passport_record_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    passport_number VARCHAR(20) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    is_valid BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_passport (passport_number)
);


```
