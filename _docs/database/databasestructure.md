---
title: "Database Structure"
permalink: /docs/database/databasestructure/
excerpt: "Complete database Structure"
last_modified_at: 2025-11-24
toc: true
---

## Complete Schema Reference

```sql

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

-- 7. UPDATE dummy verification tables with the 20 inserted users

-- Insert verification records for 20 existing users
INSERT INTO dummy_aadhar_records (aadhar_number, full_name, date_of_birth, address) VALUES
('123456789012', 'Rajesh Kumar', '1995-01-15', '123 MG Road, Mumbai, Maharashtra'),
('990000000002', 'Priya Sharma', '1990-05-20', '456 CP Avenue, Delhi, Delhi'),
('345678901234', 'Amit Patel', '1988-08-10', '789 Brigade Road, Bangalore, Karnataka'),
('990000000004', 'Sneha Desai', '1992-03-25', '321 Park Street, Pune, Maharashtra'),
('567890123456', 'Vikram Singh', '1985-11-30', '654 Mall Road, Jaipur, Rajasthan'),
('990000000006', 'Anita Verma', '1993-07-18', '234 Lake Road, Kolkata, West Bengal'),
('678901234567', 'Rahul Mehta', '1991-12-05', '567 Station Road, Ahmedabad, Gujarat'),
('990000000008', 'Kavita Nair', '1989-04-22', '890 Beach Road, Chennai, Tamil Nadu'),
('789012345678', 'Suresh Reddy', '1987-09-14', '123 Tech Park, Hyderabad, Telangana'),
('990000000010', 'Meena Iyer', '1994-06-08', '456 Garden View, Kochi, Kerala'),
('890123456789', 'Anil Gupta', '1986-02-28', '789 Civil Lines, Lucknow, Uttar Pradesh'),
('990000000012', 'Pooja Joshi', '1996-10-12', '234 University Road, Indore, Madhya Pradesh'),
('901234567890', 'Deepak Saxena', '1990-08-19', '567 Market Square, Bhopal, Madhya Pradesh'),
('990000000014', 'Neha Kapoor', '1992-11-03', '890 Hill View, Shimla, Himachal Pradesh'),
('012345678901', 'Ravi Krishnan', '1988-03-17', '123 Temple Street, Madurai, Tamil Nadu'),
('990000000016', 'Shalini Das', '1995-05-29', '456 Fort Road, Guwahati, Assam'),
('123456789013', 'Manish Yadav', '1991-07-21', '789 Railway Colony, Patna, Bihar'),
('990000000018', 'Divya Menon', '1993-09-09', '234 Beach Front, Thiruvananthapuram, Kerala'),
('234567890124', 'Arjun Pandey', '1989-12-24', '567 Ring Road, Nagpur, Maharashtra'),
('990000000020', 'Ritika Singh', '1994-01-31', '890 Cantonment, Dehradun, Uttarakhand');

INSERT INTO dummy_voter_id_records (voter_id_number, full_name, date_of_birth) VALUES
('VOT9900000001', 'Rajesh Kumar', '1995-01-15'),
('VOT2345678901', 'Priya Sharma', '1990-05-20'),
('VOT9900000003', 'Amit Patel', '1988-08-10'),
('VOT4567890123', 'Sneha Desai', '1992-03-25'),
('VOT9900000005', 'Vikram Singh', '1985-11-30'),
('VOT6789012345', 'Anita Verma', '1993-07-18'),
('VOT9900000007', 'Rahul Mehta', '1991-12-05'),
('VOT7890123456', 'Kavita Nair', '1989-04-22'),
('VOT9900000009', 'Suresh Reddy', '1987-09-14'),
('VOT8901234567', 'Meena Iyer', '1994-06-08'),
('VOT9900000011', 'Anil Gupta', '1986-02-28'),
('VOT9012345678', 'Pooja Joshi', '1996-10-12'),
('VOT9900000013', 'Deepak Saxena', '1990-08-19'),
('VOT0123456789', 'Neha Kapoor', '1992-11-03'),
('VOT9900000015', 'Ravi Krishnan', '1988-03-17'),
('VOT1234567891', 'Shalini Das', '1995-05-29'),
('VOT9900000017', 'Manish Yadav', '1991-07-21'),
('VOT2345678902', 'Divya Menon', '1993-09-09'),
('VOT9900000019', 'Arjun Pandey', '1989-12-24'),
('VOT3456789013', 'Ritika Singh', '1994-01-31');

INSERT INTO dummy_pan_records (pan_number, full_name, date_of_birth) VALUES
('ABCDE1001Z', 'Rajesh Kumar', '1995-01-15'),
('ABCDE1002Z', 'Priya Sharma', '1990-05-20'),
('ABCDE1003Z', 'Amit Patel', '1988-08-10'),
('ABCDE1004Z', 'Sneha Desai', '1992-03-25'),
('ABCDE1005Z', 'Vikram Singh', '1985-11-30'),
('ABCDE1006Z', 'Anita Verma', '1993-07-18'),
('ABCDE1007Z', 'Rahul Mehta', '1991-12-05'),
('ABCDE1008Z', 'Kavita Nair', '1989-04-22'),
('ABCDE1009Z', 'Suresh Reddy', '1987-09-14'),
('ABCDE1010Z', 'Meena Iyer', '1994-06-08'),
('ABCDE1011Z', 'Anil Gupta', '1986-02-28'),
('ABCDE1012Z', 'Pooja Joshi', '1996-10-12'),
('ABCDE1013Z', 'Deepak Saxena', '1990-08-19'),
('ABCDE1014Z', 'Neha Kapoor', '1992-11-03'),
('ABCDE1015Z', 'Ravi Krishnan', '1988-03-17'),
('ABCDE1016Z', 'Shalini Das', '1995-05-29'),
('ABCDE1017Z', 'Manish Yadav', '1991-07-21'),
('ABCDE1018Z', 'Divya Menon', '1993-09-09'),
('ABCDE1019Z', 'Arjun Pandey', '1989-12-24'),
('ABCDE1020Z', 'Ritika Singh', '1994-01-31');

INSERT INTO dummy_passport_records (passport_number, full_name, date_of_birth) VALUES
('P9900001', 'Rajesh Kumar', '1995-01-15'),
('P9900002', 'Priya Sharma', '1990-05-20'),
('P9900003', 'Amit Patel', '1988-08-10'),
('P9900004', 'Sneha Desai', '1992-03-25'),
('P9900005', 'Vikram Singh', '1985-11-30'),
('P9900006', 'Anita Verma', '1993-07-18'),
('P9900007', 'Rahul Mehta', '1991-12-05'),
('P9900008', 'Kavita Nair', '1989-04-22'),
('P9900009', 'Suresh Reddy', '1987-09-14'),
('P9900010', 'Meena Iyer', '1994-06-08'),
('P9900011', 'Anil Gupta', '1986-02-28'),
('P9900012', 'Pooja Joshi', '1996-10-12'),
('P9900013', 'Deepak Saxena', '1990-08-19'),
('P9900014', 'Neha Kapoor', '1992-11-03'),
('P9900015', 'Ravi Krishnan', '1988-03-17'),
('P9900016', 'Shalini Das', '1995-05-29'),
('P9900017', 'Manish Yadav', '1991-07-21'),
('P9900018', 'Divya Menon', '1993-09-09'),
('P9900019', 'Arjun Pandey', '1989-12-24'),
('P9900020', 'Ritika Singh', '1994-01-31');

-- Insert 10 new unassigned verification records
INSERT INTO dummy_aadhar_records (aadhar_number, full_name, date_of_birth, address) VALUES
('880000000000', 'Yadnyesh', '2000-01-01', '123 Test Lane, Test City, Test State'),
('880000000001', 'Rushikesh', '2000-01-01', '123 Test Lane, Test City, Test State'),
('880000000002', 'Aavdut', '2000-01-01', '123 Test Lane, Test City, Test State'),
('880000000003', 'Aaman', '2000-01-01', '123 Test Lane, Test City, Test State'),
('880000000004', 'Deepak', '2000-01-01', '123 Test Lane, Test City, Test State'),
('880000000005', 'Yadnyesh Kolte', '2000-01-01', '123 Test Lane, Test City, Test State'),
('880000000006', 'Rushikesh More', '2000-01-01', '123 Test Lane, Test City, Test State'),
('880000000007', 'Aavdut Joshi', '2000-01-01', '123 Test Lane, Test City, Test State'),
('880000000008', 'Aaman Sayyad', '2000-01-01', '123 Test Lane, Test City, Test State'),
('880000000009', 'Deepak Revgade', '2000-01-01', '123 Test Lane, Test City, Test State');

INSERT INTO dummy_voter_id_records (voter_id_number, full_name, date_of_birth) VALUES
('VOT8800000000', 'Yadnyesh', '2000-01-01'),
('VOT8800000001', 'Rushikesh', '2000-01-01'),
('VOT8800000002', 'Aavdut', '2000-01-01'),
('VOT8800000003', 'Aaman', '2000-01-01'),
('VOT8800000004', 'Deepak', '2000-01-01'),
('VOT8800000005', 'Yadnyesh Kolte', '2000-01-01'),
('VOT8800000006', 'Rushikesh More', '2000-01-01'),
('VOT8800000007', 'Aavdut Joshi', '2000-01-01'),
('VOT8800000008', 'Aaman Sayyad', '2000-01-01'),
('VOT8800000009', 'Deepak Revgade', '2000-01-01');

INSERT INTO dummy_pan_records (pan_number, full_name, date_of_birth) VALUES
('FGHIJ2000K', 'Yadnyesh', '2000-01-01'),
('FGHIJ2001K', 'Rushikesh', '2000-01-01'),
('FGHIJ2002K', 'Aavdut', '2000-01-01'),
('FGHIJ2003K', 'Aaman', '2000-01-01'),
('FGHIJ2004K', 'Deepak', '2000-01-01'),
('FGHIJ2005K', 'Yadnyesh Kolte', '2000-01-01'),
('FGHIJ2006K', 'Rushikesh More', '2000-01-01'),
('FGHIJ2007K', 'Aavdut Joshi', '2000-01-01'),
('FGHIJ2008K', 'Aaman Sayyad', '2000-01-01'),
('FGHIJ2009K', 'Deepak Revgade', '2000-01-01');

INSERT INTO dummy_passport_records (passport_number, full_name, date_of_birth) VALUES
('P8800000', 'Yadnyesh', '2000-01-01'),
('P8800001', 'Rushikesh', '2000-01-01'),
('P8800002', 'Aavdut', '2000-01-01'),
('P8800003', 'Aaman', '2000-01-01'),
('P8800004', 'Deepak', '2000-01-01'),
('P8800005', 'Yadnyesh Kolte', '2000-01-01'),
('P8800006', 'Rushikesh More', '2000-01-01'),
('P8800007', 'Aavdut Joshi', '2000-01-01'),
('P8800008', 'Aaman Sayyad', '2000-01-01'),
('P8800009', 'Deepak Revgade', '2000-01-01');


```
