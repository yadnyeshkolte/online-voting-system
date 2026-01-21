---
title: "One-Shot Data Setup"
permalink: /docs/database/oneshotreadydata/
excerpt: "Complete SQL script for setting up the database and inserting test data"
last_modified_at: 2026-01-21
toc: true
---

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
('VOT3456781013', 'Ritika Singh', '1994-01-31');

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



-- ============================================
-- TEST DATA INSERTION
-- ============================================


-- 1. INSERT 20 USERS WITH BCRYPT HASHED PASSWORDS
-- Updated to include both AADHAR and VOTER_ID for each user
INSERT INTO users (email, password_hash, full_name, phone_number, date_of_birth, gender, address, city, state, pincode, aadhar_number, voter_id_number, profile_image_url, is_active, is_verified, approved_at) VALUES
('rajesh.kumar@email.com', '$2a$12$h2bW9dGj8X89JMBBHjvffe2jKCerIAzPFK.LsQiy0yV3fOk/NwiV6', 'Rajesh Kumar', '9876543210', '1995-01-15', 'MALE', '123 MG Road', 'Mumbai', 'Maharashtra', '400001', '123456789012', 'VOT9900000001', '/api/user/profile/photo/1.jpg', TRUE, TRUE, '2024-01-10 10:00:00'),
('priya.sharma@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Priya Sharma', '9876543211', '1990-05-20', 'FEMALE', '456 CP Avenue', 'Delhi', 'Delhi', '110001', '990000000002', 'VOT2345678901', '/api/user/profile/photo/2.jpg', TRUE, TRUE, '2024-01-10 10:30:00'),
('amit.patel@email.com', '$2a$12$0XWI2r/7LgSn6twF9XBZWegKk85neQGOQkjdZutCEsccmAjWwQ1OK', 'Amit Patel', '9876543212', '1988-08-10', 'MALE', '789 Brigade Road', 'Bangalore', 'Karnataka', '560001', '345678901234', 'VOT9900000003', '/api/user/profile/photo/3.jpg', TRUE, TRUE, '2024-01-10 11:00:00'),
('sneha.desai@email.com', '$2a$12$GKbNrwaaUdIT0.3t.Qks8uI2KQvAvjT3WL8nK.rr9DbDEXglXm5Om', 'Sneha Desai', '9876543213', '1992-03-25', 'FEMALE', '321 Park Street', 'Pune', 'Maharashtra', '411001', '990000000004', 'VOT4567890123', '/api/user/profile/photo/4.jpg', TRUE, TRUE, '2024-01-10 11:30:00'),
('vikram.singh@email.com', '$2a$12$.IG7McJt2tLTfIzPS3msluu0mqH7KxpwAkhoeGYKDV1Ig9q5dOzra', 'Vikram Singh', '9876543214', '1985-11-30', 'MALE', '654 Mall Road', 'Jaipur', 'Rajasthan', '302001', '567890123456', 'VOT9900000005', '/api/user/profile/photo/5.jpg', TRUE, TRUE, '2024-01-10 12:00:00'),
('anita.verma@email.com', '$2a$12$rhrdrh19jqkEF5.8QcP9seYGIeC6nrZuiTicoOszmvgF64iZa21VS', 'Anita Verma', '9876543215', '1993-07-18', 'FEMALE', '234 Lake Road', 'Kolkata', 'West Bengal', '700001', '990000000006', 'VOT6789012345', '/api/user/profile/photo/6.jpg', TRUE, TRUE, '2024-01-11 09:00:00'),
('rahul.mehta@email.com', '$2a$12$vyYQkoMHbyFqOIJ0F07HoezJ/kN/UOJUYuYw.eZAdeDSAabI6zBCm', 'Rahul Mehta', '9876543216', '1991-12-05', 'MALE', '567 Station Road', 'Ahmedabad', 'Gujarat', '380001', '678901234567', 'VOT9900000007', '/api/user/profile/photo/7.jpg', TRUE, TRUE, '2024-01-11 09:30:00'),
('kavita.nair@email.com', '$2a$12$rTOeBs9Dz3IPFlPFQxyEaOrflZ3KqmKeOGA1cAbnWYb2BKt4XZPHm', 'Kavita Nair', '9876543217', '1989-04-22', 'FEMALE', '890 Beach Road', 'Chennai', 'Tamil Nadu', '600001', '990000000008', 'VOT7890123456', '/api/user/profile/photo/8.jpg', TRUE, TRUE, '2024-01-11 10:00:00'),
('suresh.reddy@email.com', '$2a$12$F2U/XiDNCaNI4Z3mqc1ITe/E3VCbsJ/mmoJS3hx//XTluntMRnxee', 'Suresh Reddy', '9876543218', '1987-09-14', 'MALE', '123 Tech Park', 'Hyderabad', 'Telangana', '500001', '789012345678', 'VOT9900000009', '/api/user/profile/photo/9.jpg', TRUE, TRUE, '2024-01-11 10:30:00'),
('meena.iyer@email.com', '$2a$12$/phBjZ5kbVnIQXFcqAyBO.JIMbtiRcWrIlF.zZauK3g.CoXcNzx2.', 'Meena Iyer', '9876543219', '1994-06-08', 'FEMALE', '456 Garden View', 'Kochi', 'Kerala', '682001', '990000000010', 'VOT8901234567', '/api/user/profile/photo/10.jpg', TRUE, TRUE, '2024-01-11 11:00:00'),
('anil.gupta@email.com', '$2a$12$JoYqmT4zrOPbTB4I/6cOw.AsaMjYB7On3n69KZ3mXa0MB22yjidC2', 'Anil Gupta', '9876543220', '1986-02-28', 'MALE', '789 Civil Lines', 'Lucknow', 'Uttar Pradesh', '226001', '890123456789', 'VOT9900000011', '/api/user/profile/photo/11.jpg', TRUE, TRUE, '2024-01-11 11:30:00'),
('pooja.joshi@email.com', '$2a$12$OlnTvuBoPFJv1jqpVmYL3OrYcdcHETymJHm8oMW.XSOmy6XQcGvje', 'Pooja Joshi', '9876543221', '1996-10-12', 'FEMALE', '234 University Road', 'Indore', 'Madhya Pradesh', '452001', '990000000012', 'VOT9012345678', '/api/user/profile/photo/12.jpg', TRUE, TRUE, '2024-01-11 12:00:00'),
('deepak.saxena@email.com', '$2a$12$HxMpfjyUH33IjL58CcdVQupc8naVaz8I7egg6/i.oRFWuVQy.FglC', 'Deepak Saxena', '9876543222', '1990-08-19', 'MALE', '567 Market Square', 'Bhopal', 'Madhya Pradesh', '462001', '901234567890', 'VOT9900000013', '/api/user/profile/photo/13.jpg', TRUE, TRUE, '2024-01-12 09:00:00'),
('neha.kapoor@email.com', '$2a$12$9gfo7Nkm/VYPpFuMrBh8neEaLEwu6cSb04y8aAu8V2ug2xtXVNvZ.', 'Neha Kapoor', '9876543223', '1992-11-03', 'FEMALE', '890 Hill View', 'Shimla', 'Himachal Pradesh', '171001', '990000000014', 'VOT0123456789', '/api/user/profile/photo/14.jpg', TRUE, TRUE, '2024-01-12 09:30:00'),
('ravi.krishnan@email.com', '$2a$12$wDVZ6It2dq1JNKMFdi0YzuNHdgn.ZLWmO5e323XHDDyJyEW7Su6BG', 'Ravi Krishnan', '9876543224', '1988-03-17', 'MALE', '123 Temple Street', 'Madurai', 'Tamil Nadu', '625001', '012345678901', 'VOT9900000015', '/api/user/profile/photo/15.jpg', TRUE, TRUE, '2024-01-12 10:00:00'),
('shalini.das@email.com', '$2a$12$AnYu/JR0ZIJltc1XegjntOimv7vCbznypzEehe8JZcN32NovvGfOG', 'Shalini Das', '9876543225', '1995-05-29', 'FEMALE', '456 Fort Road', 'Guwahati', 'Assam', '781001', '990000000016', 'VOT1234567891', '/api/user/profile/photo/16.jpg', TRUE, TRUE, '2024-01-12 10:30:00'),
('manish.yadav@email.com', '$2a$12$cixu8lsqF57fOT9nhfvFNuqtcvslyP9GYDElxLiFfT5GTPSEUaSm.', 'Manish Yadav', '9876543226', '1991-07-21', 'MALE', '789 Railway Colony', 'Patna', 'Bihar', '800001', '123456789013', 'VOT9900000017', '/api/user/profile/photo/17.jpg', TRUE, TRUE, '2024-01-12 11:00:00'),
('divya.menon@email.com', '$2a$12$7.1FJPUPpYv1CruCSoKZOOSfcE2/zDhO/ukUf/5zpSBGzAKZBSy1i', 'Divya Menon', '9876543227', '1993-09-09', 'FEMALE', '234 Beach Front', 'Thiruvananthapuram', 'Kerala', '695001', '990000000018', 'VOT2345678902', '/api/user/profile/photo/18.jpg', TRUE, TRUE, '2024-01-12 11:30:00'),
('arjun.pandey@email.com', '$2a$12$L30Zr13diUPP9NvNqenasePzijLYWYXmVwES42gYmhX7WPcWbSK5q', 'Arjun Pandey', '9876543228', '1989-12-24', 'MALE', '567 Ring Road', 'Nagpur', 'Maharashtra', '440001', '234567890124', 'VOT9900000019', '/api/user/profile/photo/19.jpg', TRUE, TRUE, '2024-01-12 12:00:00'),
('ritika.singh@email.com', '$2a$12$CJwSJuyIUvMaT5ApArqoR.kESXc1oiwv.mzn8GlDlguHhX8SV/aeS', 'Ritika Singh', '9876543229', '1994-01-31', 'FEMALE', '890 Cantonment', 'Dehradun', 'Uttarakhand', '248001', '990000000020', 'VOT3456781013', '/api/user/profile/photo/20.jpg', TRUE, TRUE, '2024-01-12 12:30:00');



-- 2. INSERT 2 ADMINS (Passwords set to 'admin123', email updated to match login)
INSERT INTO admins (email, password_hash, full_name, phone_number) VALUES
('admin@voting.com', '$2a$12$8HNsrESl9ubVI9MRaCFWY.vZZ1i6OkpmzaTnz.5nsdTGdeWEzzhym', 'Sanjay Malhotra', '9999888877'),
('admin.secondary@voting.com', '$2a$12$8HNsrESl9ubVI9MRaCFWY.vZZ1i6OkpmzaTnz.5nsdTGdeWEzzhym', 'Priyanka Chopra', '9999888878');

-- 3. INSERT 2 ACTIVE ELECTIONS AND 2 PAST ELECTIONS
INSERT INTO elections (election_name, election_type, start_date, end_date, status, result_published, result_published_at, result_published_by, created_by) VALUES
-- Active Elections
('Maharashtra State Assembly Election 2025', 'STATE', '2025-11-20 08:00:00', '2025-11-25 18:00:00', 'ACTIVE', FALSE, NULL, NULL, 1),
('Mumbai Municipal Corporation Election 2025', 'LOCAL', '2025-11-21 08:00:00', '2025-11-24 18:00:00', 'ACTIVE', FALSE, NULL, NULL, 2),
-- Past Elections (Completed)
('Karnataka State Assembly Election 2024', 'STATE', '2024-05-10 08:00:00', '2024-05-15 18:00:00', 'COMPLETED', TRUE, '2024-05-16 10:00:00', 1, 1),
('Delhi Municipal Corporation Election 2024', 'LOCAL', '2024-03-01 08:00:00', '2024-03-05 18:00:00', 'COMPLETED', TRUE, '2024-03-06 10:00:00', 2, 2);

-- 4. INSERT 5 CANDIDATES FOR ELECTION 1 (Maharashtra State) AND 5 FOR ELECTION 2 (Mumbai Municipal)
-- Candidates for Election 1 (Maharashtra State Assembly Election 2025)
INSERT INTO candidates (user_id, election_id, party_name, party_symbol, manifesto) VALUES
(1, 1, 'Indian National Congress', 'Hand', 'Development for all sections of society, focus on education and healthcare reform'),
(3, 1, 'Bharatiya Janata Party', 'Lotus', 'Infrastructure development, digital transformation, and economic growth'),
(5, 1, 'Shiv Sena (UBT)', 'Flaming Torch', 'Protection of Marathi interests, industrial development, job creation'),
(7, 1, 'Nationalist Congress Party', 'Clock', 'Agricultural reforms, farmer welfare, rural development'),
(9, 1, 'Aam Aadmi Party', 'Broom', 'Anti-corruption measures, free utilities, quality education for all');

-- Candidates for Election 2 (Mumbai Municipal Corporation Election 2025)
INSERT INTO candidates (user_id, election_id, party_name, party_symbol, manifesto) VALUES
(2, 2, 'Indian National Congress', 'Hand', 'Better civic amenities, improved public transport, waste management'),
(4, 2, 'Bharatiya Janata Party', 'Lotus', 'Smart city initiatives, improved infrastructure, efficient administration'),
(6, 2, 'Shiv Sena (UBT)', 'Flaming Torch', 'Local employment priority, better housing, Mumbai for Mumbaikars'),
(8, 2, 'Nationalist Congress Party', 'Clock', 'Water supply improvement, road maintenance, green Mumbai initiative'),
(10, 2, 'Maharashtra Navnirman Sena', 'Railway Engine', 'Focus on middle-class welfare, affordable housing, traffic solutions');


-- 5. INSERT VOTES (17 users voted in Election 1, 18 users voted in Election 2)
-- Election 1 votes (Users 1-17 voted, Users 18-20 did NOT vote)
INSERT INTO votes (election_id, user_id, candidate_id, vote_hash, voted_at) VALUES
(1, 1, 1, CONCAT('hash_e1_u1_c1_', MD5(CONCAT('1', '1', '1', NOW()))), '2025-11-20 09:15:00'),
(1, 2, 2, CONCAT('hash_e1_u2_c2_', MD5(CONCAT('1', '2', '2', NOW()))), '2025-11-20 10:30:00'),
(1, 3, 3, CONCAT('hash_e1_u3_c3_', MD5(CONCAT('1', '3', '3', NOW()))), '2025-11-20 11:45:00'),
(1, 4, 1, CONCAT('hash_e1_u4_c1_', MD5(CONCAT('1', '4', '1', NOW()))), '2025-11-20 13:20:00'),
(1, 5, 2, CONCAT('hash_e1_u5_c2_', MD5(CONCAT('1', '5', '2', NOW()))), '2025-11-20 14:10:00'),
(1, 6, 4, CONCAT('hash_e1_u6_c4_', MD5(CONCAT('1', '6', '4', NOW()))), '2025-11-20 15:00:00'),
(1, 7, 5, CONCAT('hash_e1_u7_c5_', MD5(CONCAT('1', '7', '5', NOW()))), '2025-11-20 15:45:00'),
(1, 8, 1, CONCAT('hash_e1_u8_c1_', MD5(CONCAT('1', '8', '1', NOW()))), '2025-11-20 16:20:00'),
(1, 9, 2, CONCAT('hash_e1_u9_c2_', MD5(CONCAT('1', '9', '2', NOW()))), '2025-11-21 09:00:00'),
(1, 10, 3, CONCAT('hash_e1_u10_c3_', MD5(CONCAT('1', '10', '3', NOW()))), '2025-11-21 10:15:00'),
(1, 11, 4, CONCAT('hash_e1_u11_c4_', MD5(CONCAT('1', '11', '4', NOW()))), '2025-11-21 11:30:00'),
(1, 12, 1, CONCAT('hash_e1_u12_c1_', MD5(CONCAT('1', '12', '1', NOW()))), '2025-11-21 12:45:00'),
(1, 13, 2, CONCAT('hash_e1_u13_c2_', MD5(CONCAT('1', '13', '2', NOW()))), '2025-11-21 13:50:00'),
(1, 14, 5, CONCAT('hash_e1_u14_c5_', MD5(CONCAT('1', '14', '5', NOW()))), '2025-11-21 14:30:00'),
(1, 15, 3, CONCAT('hash_e1_u15_c3_', MD5(CONCAT('1', '15', '3', NOW()))), '2025-11-21 15:15:00'),
(1, 16, 1, CONCAT('hash_e1_u16_c1_', MD5(CONCAT('1', '16', '1', NOW()))), '2025-11-21 16:00:00'),
(1, 17, 2, CONCAT('hash_e1_u17_c2_', MD5(CONCAT('1', '17', '2', NOW()))), '2025-11-21 16:45:00');

-- Election 2 votes (Users 1-18 voted, Users 19-20 did NOT vote)
INSERT INTO votes (election_id, user_id, candidate_id, vote_hash, voted_at) VALUES
(2, 1, 6, CONCAT('hash_e2_u1_c6_', MD5(CONCAT('2', '1', '6', NOW()))), '2025-11-21 09:30:00'),
(2, 2, 7, CONCAT('hash_e2_u2_c7_', MD5(CONCAT('2', '2', '7', NOW()))), '2025-11-21 10:00:00'),
(2, 3, 8, CONCAT('hash_e2_u3_c8_', MD5(CONCAT('2', '3', '8', NOW()))), '2025-11-21 11:15:00'),
(2, 4, 6, CONCAT('hash_e2_u4_c6_', MD5(CONCAT('2', '4', '6', NOW()))), '2025-11-21 12:00:00'),
(2, 5, 7, CONCAT('hash_e2_u5_c7_', MD5(CONCAT('2', '5', '7', NOW()))), '2025-11-21 13:10:00'),
(2, 6, 9, CONCAT('hash_e2_u6_c9_', MD5(CONCAT('2', '6', '9', NOW()))), '2025-11-21 14:00:00'),
(2, 7, 10, CONCAT('hash_e2_u7_c10_', MD5(CONCAT('2', '7', '10', NOW()))), '2025-11-21 14:45:00'),
(2, 8, 6, CONCAT('hash_e2_u8_c6_', MD5(CONCAT('2', '8', '6', NOW()))), '2025-11-21 15:30:00'),
(2, 9, 7, CONCAT('hash_e2_u9_c7_', MD5(CONCAT('2', '9', '7', NOW()))), '2025-11-21 16:15:00'),
(2, 10, 8, CONCAT('hash_e2_u10_c8_', MD5(CONCAT('2', '10', '8', NOW()))), '2025-11-22 09:00:00'),
(2, 11, 9, CONCAT('hash_e2_u11_c9_', MD5(CONCAT('2', '11', '9', NOW()))), '2025-11-22 10:00:00'),
(2, 12, 6, CONCAT('hash_e2_u12_c6_', MD5(CONCAT('2', '12', '6', NOW()))), '2025-11-22 11:00:00'),
(2, 13, 7, CONCAT('hash_e2_u13_c7_', MD5(CONCAT('2', '13', '7', NOW()))), '2025-11-22 12:00:00'),
(2, 14, 10, CONCAT('hash_e2_u14_c10_', MD5(CONCAT('2', '14', '10', NOW()))), '2025-11-22 13:00:00'),
(2, 15, 8, CONCAT('hash_e2_u15_c8_', MD5(CONCAT('2', '15', '8', NOW()))), '2025-11-22 14:00:00'),
(2, 16, 6, CONCAT('hash_e2_u16_c6_', MD5(CONCAT('2', '16', '6', NOW()))), '2025-11-22 15:00:00'),
(2, 17, 7, CONCAT('hash_e2_u17_c7_', MD5(CONCAT('2', '17', '7', NOW()))), '2025-11-22 16:00:00'),
(2, 18, 8, CONCAT('hash_e2_u18_c8_', MD5(CONCAT('2', '18', '8', NOW()))), '2025-11-22 16:30:00');


-- UPDATE voter_election_status for all users in both active elections
-- Election 1: Users 1-17 voted, Users 18-20 did NOT vote
INSERT INTO voter_election_status (election_id, user_id, has_voted, voted_at) VALUES
(1, 1, TRUE, '2025-11-20 09:15:00'),
(1, 2, TRUE, '2025-11-20 10:30:00'),
(1, 3, TRUE, '2025-11-20 11:45:00'),
(1, 4, TRUE, '2025-11-20 13:20:00'),
(1, 5, TRUE, '2025-11-20 14:10:00'),
(1, 6, TRUE, '2025-11-20 15:00:00'),
(1, 7, TRUE, '2025-11-20 15:45:00'),
(1, 8, TRUE, '2025-11-20 16:20:00'),
(1, 9, TRUE, '2025-11-21 09:00:00'),
(1, 10, TRUE, '2025-11-21 10:15:00'),
(1, 11, TRUE, '2025-11-21 11:30:00'),
(1, 12, TRUE, '2025-11-21 12:45:00'),
(1, 13, TRUE, '2025-11-21 13:50:00'),
(1, 14, TRUE, '2025-11-21 14:30:00'),
(1, 15, TRUE, '2025-11-21 15:15:00'),
(1, 16, TRUE, '2025-11-21 16:00:00'),
(1, 17, TRUE, '2025-11-21 16:45:00'),
(1, 18, FALSE, NULL),
(1, 19, FALSE, NULL),
(1, 20, FALSE, NULL);

-- Election 2: Users 1-18 voted, Users 19-20 did NOT vote
INSERT INTO voter_election_status (election_id, user_id, has_voted, voted_at) VALUES
(2, 1, TRUE, '2025-11-21 09:30:00'),
(2, 2, TRUE, '2025-11-21 10:00:00'),
(2, 3, TRUE, '2025-11-21 11:15:00'),
(2, 4, TRUE, '2025-11-21 12:00:00'),
(2, 5, TRUE, '2025-11-21 13:10:00'),
(2, 6, TRUE, '2025-11-21 14:00:00'),
(2, 7, TRUE, '2025-11-21 14:45:00'),
(2, 8, TRUE, '2025-11-21 15:30:00'),
(2, 9, TRUE, '2025-11-21 16:15:00'),
(2, 10, TRUE, '2025-11-22 09:00:00'),
(2, 11, TRUE, '2025-11-22 10:00:00'),
(2, 12, TRUE, '2025-11-22 11:00:00'),
(2, 13, TRUE, '2025-11-22 12:00:00'),
(2, 14, TRUE, '2025-11-22 13:00:00'),
(2, 15, TRUE, '2025-11-22 14:00:00'),
(2, 16, TRUE, '2025-11-22 15:00:00'),
(2, 17, TRUE, '2025-11-22 16:00:00'),
(2, 18, TRUE, '2025-11-22 16:30:00'),
(2, 19, FALSE, NULL),
(2, 20, FALSE, NULL);

-- 6. UPDATE election_results table (for active elections - current vote counts)
-- Election 1 Results (Maharashtra State - 17 votes cast)
-- Candidate 1 (user_id 1): 5 votes
-- Candidate 2 (user_id 3): 6 votes
-- Candidate 3 (user_id 5): 3 votes
-- Candidate 4 (user_id 7): 2 votes
-- Candidate 5 (user_id 9): 1 vote
INSERT INTO election_results (election_id, candidate_id, vote_count, vote_percentage, rank_position) VALUES
(1, 1, 5, 29.41, 2),
(1, 2, 6, 35.29, 1),
(1, 3, 3, 17.65, 3),
(1, 4, 2, 11.76, 4),
(1, 5, 1, 5.88, 5);

-- Election 2 Results (Mumbai Municipal - 18 votes cast)
-- Candidate 6 (user_id 2): 6 votes
-- Candidate 7 (user_id 4): 6 votes
-- Candidate 8 (user_id 6): 4 votes
-- Candidate 9 (user_id 8): 1 vote
-- Candidate 10 (user_id 10): 1 vote
INSERT INTO election_results (election_id, candidate_id, vote_count, vote_percentage, rank_position) VALUES
(2, 6, 6, 33.33, 1),
(2, 7, 6, 33.33, 1),
(2, 8, 4, 22.22, 3),
(2, 9, 1, 5.56, 4),
(2, 10, 1, 5.56, 4);

-- UPDATE election_reports table
-- Report for Election 1 (Maharashtra State Assembly)
INSERT INTO election_reports (election_id, total_registered_voters, total_votes_cast, voter_turnout_percentage, total_candidates, winning_candidate_id, winning_margin, report_generated_by) VALUES
(1, 20, 17, 85.00, 5, 2, 1, 1);

-- Report for Election 2 (Mumbai Municipal Corporation)
INSERT INTO election_reports (election_id, total_registered_voters, total_votes_cast, voter_turnout_percentage, total_candidates, winning_candidate_id, winning_margin, report_generated_by) VALUES
(2, 20, 18, 90.00, 5, 6, 0, 2);


-- 7. NEW USERS AND LOCATION UPDATES (January 20, 2026)
-- Update Existing Elections with specific locations
UPDATE elections SET state = 'Maharashtra', election_type = 'STATE' WHERE election_name = 'Maharashtra State Assembly Election 2025';
UPDATE elections SET city = 'Mumbai', state = 'Maharashtra', election_type = 'LOCAL' WHERE election_name = 'Mumbai Municipal Corporation Election 2025';
UPDATE elections SET state = 'Karnataka', election_type = 'STATE' WHERE election_name = 'Karnataka State Assembly Election 2024';
UPDATE elections SET city = 'Delhi', state = 'Delhi', election_type = 'LOCAL' WHERE election_name = 'Delhi Municipal Corporation Election 2024';

-- Insert 15 New Users
INSERT INTO users (email, password_hash, full_name, phone_number, date_of_birth, gender, address, city, state, pincode, aadhar_number, voter_id_number, is_active, is_verified, approved_at) VALUES
('madhur.chaudhari@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Madhur Chaudhari', '9000000001', '2000-01-01', 'MALE', 'Jalgaon City', 'Jalgaon', 'Maharashtra', '425001', '100000000001', 'VOT1000000001', TRUE, TRUE, NOW()),
('anuj.tomar@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Anuj Tomar', '9000000002', '2000-01-01', 'MALE', 'Agra City', 'Agra', 'Uttar Pradesh', '282001', '100000000002', 'VOT1000000002', TRUE, TRUE, NOW()),
('satyam.patel@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Satyam Patel', '9000000003', '2000-01-01', 'MALE', 'Katni City', 'Katni', 'Madhya Pradesh', '483501', '100000000003', 'VOT1000000003', TRUE, TRUE, NOW()),
('soham.dey@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Soham Kumar Dey', '9000000004', '2000-01-01', 'MALE', 'Murshidabad City', 'Murshidabad', 'West Bengal', '742101', '100000000004', 'VOT1000000004', TRUE, TRUE, NOW()),
('utkarsh.phalphale@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Utkarsh Phalphale', '9000000005', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '411001', '100000000005', 'VOT1000000005', TRUE, TRUE, NOW()),
('rajat.morya@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Rajat Morya', '9000000006', '2000-01-01', 'MALE', 'Bareilly City', 'Bareilly', 'Uttar Pradesh', '243001', '100000000006', 'VOT1000000006', TRUE, TRUE, NOW()),
('ayush.kush@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Ayush Kush', '9000000007', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '226001', '100000000007', 'VOT1000000007', TRUE, TRUE, NOW()),
('ninad.soman@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Ninad Soman', '9000000008', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '411004', '100000000008', 'VOT1000000008', TRUE, TRUE, NOW()),
('vivek.yadav@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Vivek Yadav', '9000000009', '2000-01-01', 'MALE', 'Nalanda City', 'Nalanda', 'Bihar', '803111', '100000000009', 'VOT1000000009', TRUE, TRUE, NOW()),
('satyam.bavankar@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Satyam Bavankar', '9000000010', '2000-01-01', 'MALE', 'Seoni City', 'Seoni', 'Madhya Pradesh', '480661', '100000000010', 'VOT1000000010', TRUE, TRUE, NOW()),
('tanay.mapare@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Tanay Mapare', '9000000011', '2000-01-01', 'MALE', 'Washim City', 'Washim', 'Maharashtra', '444505', '100000000011', 'VOT1000000011', TRUE, TRUE, NOW()),
('ayush.shrivastava@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Ayush Shrivastava', '9000000012', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '226002', '100000000012', 'VOT1000000012', TRUE, TRUE, NOW()),
('ansh.mittal@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Ansh Mittal', '9000000013', '2000-01-01', 'MALE', 'Gwalior City', 'Gwalior', 'Madhya Pradesh', '474001', '100000000013', 'VOT1000000013', TRUE, TRUE, NOW()),
('rajdeep.kala@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Rajdeep Kala', '9000000014', '2000-01-01', 'MALE', 'Ujjain City', 'Ujjain', 'Madhya Pradesh', '456001', '100000000014', 'VOT1000000014', TRUE, TRUE, NOW()),
('satyendra.rathore@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Satyendra Rathore', '9000000015', '2000-01-01', 'MALE', 'Ujjain City', 'Ujjain', 'Madhya Pradesh', '456001', '100000000015', 'VOT1000000015', TRUE, TRUE, NOW());

-- Insert 15 Additional Users (Group 2)
INSERT INTO users (email, password_hash, full_name, phone_number, date_of_birth, gender, address, city, state, pincode, aadhar_number, voter_id_number, is_active, is_verified, approved_at) VALUES
('prabal.singh@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Prabal Singh', '9000000016', '2000-01-01', 'MALE', 'Aligarh City', 'Aligarh', 'Uttar Pradesh', '111111', '100000000016', 'VOT1000000016', TRUE, TRUE, NOW()),
('akash.dwivedi@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Akash Dwivedi', '9000000017', '2000-01-01', 'MALE', 'Sidhi City', 'Sidhi', 'Madhya Pradesh', '111111', '100000000017', 'VOT1000000017', TRUE, TRUE, NOW()),
('priyanshu.raj@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Priyanshu Raj', '9000000018', '2000-01-01', 'MALE', 'Bilaspur City', 'Bilaspur', 'Chhattisgarh', '111111', '100000000018', 'VOT1000000018', TRUE, TRUE, NOW()),
('atul.pawar@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Atul Pawar', '9000000019', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '111111', '100000000019', 'VOT1000000019', TRUE, TRUE, NOW()),
('himanshu.muleva@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Himanshu Muleva', '9000000020', '2000-01-01', 'MALE', 'Indore City', 'Indore', 'Madhya Pradesh', '111111', '100000000020', 'VOT1000000020', TRUE, TRUE, NOW()),
('anuj.sharma@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Anuj Sharma', '9000000021', '2000-01-01', 'MALE', 'Agra City', 'Agra', 'Uttar Pradesh', '111111', '100000000021', 'VOT1000000021', TRUE, TRUE, NOW()),
('akash.rout@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Akash Rout', '9000000022', '2000-01-01', 'MALE', 'Belagavi City', 'Belagavi', 'Karnataka', '111111', '100000000022', 'VOT1000000022', TRUE, TRUE, NOW()),
('pratinav@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Pratinav', '9000000023', '2000-01-01', 'MALE', 'Varanasi City', 'Varanasi', 'Uttar Pradesh', '111111', '100000000023', 'VOT1000000023', TRUE, TRUE, NOW()),
('satyabrat.panigrahi@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Satyabrat Panigrahi', '9000000024', '2000-01-01', 'MALE', 'Bhadrak City', 'Bhadrak', 'Odisha', '111111', '100000000024', 'VOT1000000024', TRUE, TRUE, NOW()),
('rohan.sinha@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Rohan Sinha', '9000000025', '2000-01-01', 'MALE', 'Durg City', 'Durg', 'Chhattisgarh', '111111', '100000000025', 'VOT1000000025', TRUE, TRUE, NOW()),
('nishant.vij@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Nishant Vij', '9000000026', '2000-01-01', 'MALE', 'Sirsa City', 'Sirsa', 'Haryana', '111111', '100000000026', 'VOT1000000026', TRUE, TRUE, NOW()),
('pritish.bhatiya@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Pritish Bhatiya', '9000000027', '2000-01-01', 'MALE', 'Jabalpur City', 'Jabalpur', 'Madhya Pradesh', '111111', '100000000027', 'VOT1000000027', TRUE, TRUE, NOW()),
('apoorv.singh@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Apoorv Singh', '9000000028', '2000-01-01', 'MALE', 'Mathura City', 'Mathura', 'Uttar Pradesh', '111111', '100000000028', 'VOT1000000028', TRUE, TRUE, NOW()),
('pranav.tiwari@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Pranav Tiwari', '9000000029', '2000-01-01', 'MALE', 'Ghaziabad City', 'Ghaziabad', 'Uttar Pradesh', '111111', '100000000029', 'VOT1000000029', TRUE, TRUE, NOW()),
('abhijeet.singh@email.com', '$2a$12$vBI2Llf/b1f1z3xr3r2g5.NOnPl2oRNzBFY5E2HQg1MrH.hOv9v12', 'Abhijeet Singh', '9000000030', '2000-01-01', 'MALE', 'Indore City', 'Indore', 'Madhya Pradesh', '111111', '100000000030', 'VOT1000000030', TRUE, TRUE, NOW());


-- ============================================
-- MISSING DATA FOR KARNATAKA AND DELHI ELECTIONS
-- ============================================

-- 4. INSERT CANDIDATES FOR ELECTION 3 (Karnataka) AND ELECTION 4 (Delhi)
-- Candidates for Election 3 (Karnataka State Assembly Election 2024)
-- IDs will be 11, 12, 13, 14, 15
INSERT INTO candidates (user_id, election_id, party_name, party_symbol, manifesto) VALUES
(11, 3, 'Bharatiya Janata Party', 'Lotus', 'Focus on urban infrastructure and tech growth'),
(12, 3, 'Indian National Congress', 'Hand', 'Welfare schemes for women and youth'),
(13, 3, 'Janata Dal (Secular)', 'Woman carrying paddy sheaf', 'Farmer support and rural development'),
(14, 3, 'Aam Aadmi Party', 'Broom', 'Quality education and healthcare for all'),
(15, 3, 'Independent', 'Kite', 'Voice for the local community issues');

-- Candidates for Election 4 (Delhi Municipal Corporation Election 2024)
-- IDs will be 16, 17, 18, 19, 20
INSERT INTO candidates (user_id, election_id, party_name, party_symbol, manifesto) VALUES
(16, 4, 'Aam Aadmi Party', 'Broom', 'Clean Delhi, Green Delhi initiative'),
(17, 4, 'Bharatiya Janata Party', 'Lotus', 'Efficient waste management and civic amenities'),
(18, 4, 'Indian National Congress', 'Hand', 'Empowering local governance bodies'),
(19, 4, 'Independent', 'Bat', 'Traffic decongestion and road safety'),
(20, 4, 'Bahujan Samaj Party', 'Elephant', 'Social justice and equal rights');

-- 5. INSERT VOTES
-- Election 3 votes (Users 1-15 voted)
INSERT INTO votes (election_id, user_id, candidate_id, vote_hash, voted_at) VALUES
(3, 1, 11, CONCAT('hash_e3_u1_c11_', MD5(CONCAT('3', '1', '11', NOW()))), '2024-05-10 09:00:00'),
(3, 2, 12, CONCAT('hash_e3_u2_c12_', MD5(CONCAT('3', '2', '12', NOW()))), '2024-05-10 09:30:00'),
(3, 3, 11, CONCAT('hash_e3_u3_c11_', MD5(CONCAT('3', '3', '11', NOW()))), '2024-05-10 10:00:00'),
(3, 4, 13, CONCAT('hash_e3_u4_c13_', MD5(CONCAT('3', '4', '13', NOW()))), '2024-05-10 10:45:00'),
(3, 5, 11, CONCAT('hash_e3_u5_c11_', MD5(CONCAT('3', '5', '11', NOW()))), '2024-05-10 11:15:00'),
(3, 6, 12, CONCAT('hash_e3_u6_c12_', MD5(CONCAT('3', '6', '12', NOW()))), '2024-05-10 12:00:00'),
(3, 7, 14, CONCAT('hash_e3_u7_c14_', MD5(CONCAT('3', '7', '14', NOW()))), '2024-05-10 12:45:00'),
(3, 8, 11, CONCAT('hash_e3_u8_c11_', MD5(CONCAT('3', '8', '11', NOW()))), '2024-05-10 13:30:00'),
(3, 9, 15, CONCAT('hash_e3_u9_c15_', MD5(CONCAT('3', '9', '15', NOW()))), '2024-05-10 14:00:00'),
(3, 10, 11, CONCAT('hash_e3_u10_c11_', MD5(CONCAT('3', '10', '11', NOW()))), '2024-05-10 15:00:00'),
(3, 11, 12, CONCAT('hash_e3_u11_c12_', MD5(CONCAT('3', '11', '12', NOW()))), '2024-05-11 09:00:00'),
(3, 12, 11, CONCAT('hash_e3_u12_c11_', MD5(CONCAT('3', '12', '11', NOW()))), '2024-05-11 10:00:00'),
(3, 13, 13, CONCAT('hash_e3_u13_c13_', MD5(CONCAT('3', '13', '13', NOW()))), '2024-05-11 11:00:00'),
(3, 14, 11, CONCAT('hash_e3_u14_c11_', MD5(CONCAT('3', '14', '11', NOW()))), '2024-05-11 12:00:00'),
(3, 15, 12, CONCAT('hash_e3_u15_c12_', MD5(CONCAT('3', '15', '12', NOW()))), '2024-05-11 13:00:00');

-- Election 4 votes (Users 1-16 voted)
INSERT INTO votes (election_id, user_id, candidate_id, vote_hash, voted_at) VALUES
(4, 1, 16, CONCAT('hash_e4_u1_c16_', MD5(CONCAT('4', '1', '16', NOW()))), '2024-03-01 09:00:00'),
(4, 2, 17, CONCAT('hash_e4_u2_c17_', MD5(CONCAT('4', '2', '17', NOW()))), '2024-03-01 09:45:00'),
(4, 3, 16, CONCAT('hash_e4_u3_c16_', MD5(CONCAT('4', '3', '16', NOW()))), '2024-03-01 10:15:00'),
(4, 4, 18, CONCAT('hash_e4_u4_c18_', MD5(CONCAT('4', '4', '18', NOW()))), '2024-03-01 11:00:00'),
(4, 5, 16, CONCAT('hash_e4_u5_c16_', MD5(CONCAT('4', '5', '16', NOW()))), '2024-03-01 11:45:00'),
(4, 6, 19, CONCAT('hash_e4_u6_c19_', MD5(CONCAT('4', '6', '19', NOW()))), '2024-03-01 12:30:00'),
(4, 7, 16, CONCAT('hash_e4_u7_c16_', MD5(CONCAT('4', '7', '16', NOW()))), '2024-03-01 13:15:00'),
(4, 8, 17, CONCAT('hash_e4_u8_c17_', MD5(CONCAT('4', '8', '17', NOW()))), '2024-03-01 14:00:00'),
(4, 9, 16, CONCAT('hash_e4_u9_c16_', MD5(CONCAT('4', '9', '16', NOW()))), '2024-03-01 15:00:00'),
(4, 10, 20, CONCAT('hash_e4_u10_c20_', MD5(CONCAT('4', '10', '20', NOW()))), '2024-03-01 16:00:00'),
(4, 11, 16, CONCAT('hash_e4_u11_c16_', MD5(CONCAT('4', '11', '16', NOW()))), '2024-03-02 09:00:00'),
(4, 12, 17, CONCAT('hash_e4_u12_c17_', MD5(CONCAT('4', '12', '17', NOW()))), '2024-03-02 10:00:00'),
(4, 13, 16, CONCAT('hash_e4_u13_c16_', MD5(CONCAT('4', '13', '16', NOW()))), '2024-03-02 11:00:00'),
(4, 14, 18, CONCAT('hash_e4_u14_c18_', MD5(CONCAT('4', '14', '18', NOW()))), '2024-03-02 12:00:00'),
(4, 15, 16, CONCAT('hash_e4_u15_c16_', MD5(CONCAT('4', '15', '16', NOW()))), '2024-03-02 13:00:00'),
(4, 16, 17, CONCAT('hash_e4_u16_c17_', MD5(CONCAT('4', '16', '17', NOW()))), '2024-03-02 14:00:00');

-- 6. UPDATE voter_election_status
-- Election 3: Users 1-15 voted, 16-20 did not
INSERT INTO voter_election_status (election_id, user_id, has_voted, voted_at) VALUES
(3, 1, TRUE, '2024-05-10 09:00:00'),
(3, 2, TRUE, '2024-05-10 09:30:00'),
(3, 3, TRUE, '2024-05-10 10:00:00'),
(3, 4, TRUE, '2024-05-10 10:45:00'),
(3, 5, TRUE, '2024-05-10 11:15:00'),
(3, 6, TRUE, '2024-05-10 12:00:00'),
(3, 7, TRUE, '2024-05-10 12:45:00'),
(3, 8, TRUE, '2024-05-10 13:30:00'),
(3, 9, TRUE, '2024-05-10 14:00:00'),
(3, 10, TRUE, '2024-05-10 15:00:00'),
(3, 11, TRUE, '2024-05-11 09:00:00'),
(3, 12, TRUE, '2024-05-11 10:00:00'),
(3, 13, TRUE, '2024-05-11 11:00:00'),
(3, 14, TRUE, '2024-05-11 12:00:00'),
(3, 15, TRUE, '2024-05-11 13:00:00'),
(3, 16, FALSE, NULL),
(3, 17, FALSE, NULL),
(3, 18, FALSE, NULL),
(3, 19, FALSE, NULL),
(3, 20, FALSE, NULL);

-- Election 4: Users 1-16 voted, 17-20 did not
INSERT INTO voter_election_status (election_id, user_id, has_voted, voted_at) VALUES
(4, 1, TRUE, '2024-03-01 09:00:00'),
(4, 2, TRUE, '2024-03-01 09:45:00'),
(4, 3, TRUE, '2024-03-01 10:15:00'),
(4, 4, TRUE, '2024-03-01 11:00:00'),
(4, 5, TRUE, '2024-03-01 11:45:00'),
(4, 6, TRUE, '2024-03-01 12:30:00'),
(4, 7, TRUE, '2024-03-01 13:15:00'),
(4, 8, TRUE, '2024-03-01 14:00:00'),
(4, 9, TRUE, '2024-03-01 15:00:00'),
(4, 10, TRUE, '2024-03-01 16:00:00'),
(4, 11, TRUE, '2024-03-02 09:00:00'),
(4, 12, TRUE, '2024-03-02 10:00:00'),
(4, 13, TRUE, '2024-03-02 11:00:00'),
(4, 14, TRUE, '2024-03-02 12:00:00'),
(4, 15, TRUE, '2024-03-02 13:00:00'),
(4, 16, TRUE, '2024-03-02 14:00:00'),
(4, 17, FALSE, NULL),
(4, 18, FALSE, NULL),
(4, 19, FALSE, NULL),
(4, 20, FALSE, NULL);

-- 7. UPDATE election_results
-- Election 3 Results (Karnataka - 15 votes)
-- Candidate 11: 7 votes (Users 1,3,5,8,10,12,14)
-- Candidate 12: 4 votes (Users 2,6,11,15)
-- Candidate 13: 2 votes (Users 4,13)
-- Candidate 14: 1 vote (User 7)
-- Candidate 15: 1 vote (User 9)
INSERT INTO election_results (election_id, candidate_id, vote_count, vote_percentage, rank_position) VALUES
(3, 11, 7, 46.67, 1),
(3, 12, 4, 26.67, 2),
(3, 13, 2, 13.33, 3),
(3, 14, 1, 6.67, 4),
(3, 15, 1, 6.67, 4);

-- Election 4 Results (Delhi - 16 votes)
-- Candidate 16: 8 votes (Users 1,3,5,7,9,11,13,15)
-- Candidate 17: 4 votes (Users 2,8,12,16)
-- Candidate 18: 2 votes (Users 4,14)
-- Candidate 19: 1 vote (User 6)
-- Candidate 20: 1 vote (User 10)
INSERT INTO election_results (election_id, candidate_id, vote_count, vote_percentage, rank_position) VALUES
(4, 16, 8, 50.00, 1),
(4, 17, 4, 25.00, 2),
(4, 18, 2, 12.50, 3),
(4, 19, 1, 6.25, 4),
(4, 20, 1, 6.25, 4);

-- UPDATE election_reports
-- Report for Election 3 (Karnataka)
INSERT INTO election_reports (election_id, total_registered_voters, total_votes_cast, voter_turnout_percentage, total_candidates, winning_candidate_id, winning_margin, report_generated_by) VALUES
(3, 20, 15, 75.00, 5, 11, 3, 1);

-- Report for Election 4 (Delhi)
INSERT INTO election_reports (election_id, total_registered_voters, total_votes_cast, voter_turnout_percentage, total_candidates, winning_candidate_id, winning_margin, report_generated_by) VALUES
(4, 20, 16, 80.00, 5, 16, 4, 2);

-- 8. UPDATE PASSWORDS TO MATCH FIRSTNAME+LASTNAME (Concatenated)
-- Generated BCrypt hashes (cost 12)
UPDATE users SET password_hash = '$2b$12$LRKGisaaXknMnsDUShcO4ugcvMASq0V2eYOI2SeMFivIOAxQNZp86' WHERE full_name = 'Rajesh Kumar';
UPDATE users SET password_hash = '$2b$12$1LsyuVhJytA2cd/8yJFHN.5ZyNp.IXVRC/fFoTGbxBX.aYZlL55Ou' WHERE full_name = 'Priya Sharma';
UPDATE users SET password_hash = '$2b$12$9RWpzRPTPvWDkwidMUWzyO9BoK/9EkLWcaJ0k6I46dfrbmhXPDELK' WHERE full_name = 'Amit Patel';
UPDATE users SET password_hash = '$2b$12$btBbWEl.zXOUOH5LVzSHueSpKkDip4.LwX5PWWcnyZqY/6DedHXmC' WHERE full_name = 'Sneha Desai';
UPDATE users SET password_hash = '$2b$12$6mlvweTQouNs/fMv6lZ0s.n.NXxmaYL9KUWl2k29qyB4L/ECjFi4K' WHERE full_name = 'Vikram Singh';
UPDATE users SET password_hash = '$2b$12$qAlJfh2JmYiO2L0fJceDWeys.QAu8cbJd53aBqsOMBqwMcK5.2qeO' WHERE full_name = 'Anita Verma';
UPDATE users SET password_hash = '$2b$12$lHfk2Y1BxQCZAwJCOwEQquewsL3/HJ540cZa/FWUSXd6V2drJmIOm' WHERE full_name = 'Rahul Mehta';
UPDATE users SET password_hash = '$2b$12$AuovYys4/2yz1BKfb34fPOMHgh/rl1fEQEafh/Ho3SbyjjMyYjeBq' WHERE full_name = 'Kavita Nair';
UPDATE users SET password_hash = '$2b$12$iIU9L1MEnows/Q84DU0kBuMdqu11i9Pe2T18JtL6XE3eMv7IHEAwK' WHERE full_name = 'Suresh Reddy';
UPDATE users SET password_hash = '$2b$12$Dyk7vOoHPt9OTVttU1kAMuTn.jiPIE60VXSn2p/zfPSFRyMkEAnti' WHERE full_name = 'Meena Iyer';
UPDATE users SET password_hash = '$2b$12$ElN.gaBxJYee2tzHXuc8g.nl74oGKvgB.OHOSAevHk.h4Yy/tHjWW' WHERE full_name = 'Anil Gupta';
UPDATE users SET password_hash = '$2b$12$Xo1RkRceppO.JMK37PL8PeBruKuoIlIoUi6Qk3hOj4tpkwRhpzUsW' WHERE full_name = 'Pooja Joshi';
UPDATE users SET password_hash = '$2b$12$Al0A7W.G42Mm6/1/5l1WU.39L/crVht6a0Xc/wvthfgvO6za/w/ue' WHERE full_name = 'Deepak Saxena';
UPDATE users SET password_hash = '$2b$12$NxcjIrQ0Cls6nndMvBPhbuSfdn6DwFTbJzyTAGB2Uo/NlwD9nQ0Ke' WHERE full_name = 'Neha Kapoor';
UPDATE users SET password_hash = '$2b$12$50tua8x5T.kKuQ/yTnGImOE1K.KMBozj6wZHw9v8b5UPeHaD9bNTC' WHERE full_name = 'Ravi Krishnan';
UPDATE users SET password_hash = '$2b$12$LyYpetvoGqOkOUtnGxmwHOX1ImoQtcidaJd5haxgaymUsjUp1r4ju' WHERE full_name = 'Shalini Das';
UPDATE users SET password_hash = '$2b$12$4Sx2iUPutyVT7PWh/KhJXuLQyb5aQCAJabFOupmHkO.1yjUlp0swK' WHERE full_name = 'Manish Yadav';
UPDATE users SET password_hash = '$2b$12$ij1M3UsiI9Ifm3BuivSY6OS6beYnewXlKqdphJwi1SLWHUHzuHV6C' WHERE full_name = 'Divya Menon';
UPDATE users SET password_hash = '$2b$12$piBODqC8yPlKCVv9aFpFjunvSKUVc29fBUshfPgiBkpXA9nYX5ADO' WHERE full_name = 'Arjun Pandey';
UPDATE users SET password_hash = '$2b$12$/pSsDgV04YY1scj2Izz7ueyfItYodwDHZz1QvbA5FNqAXUOVOvcOC' WHERE full_name = 'Ritika Singh';
UPDATE users SET password_hash = '$2b$12$XV1zyIMfPfLQEQ6beTDdI.dFXz9ZtESBxQ0Hzw.Sw6zkMwYEBpKFm' WHERE full_name = 'Madhur Chaudhari';
UPDATE users SET password_hash = '$2b$12$HcobJJF2CnPEBQudKUYoF.oTIMm2GhlfOYmGjwB5JTgZm/2lW6wsa' WHERE full_name = 'Anuj Tomar';
UPDATE users SET password_hash = '$2b$12$Nu7k/OvJ375ge1q059vNMu5c6iCkCfYccpqwjhU8mPVMIPurJnRGm' WHERE full_name = 'Satyam Patel';
UPDATE users SET password_hash = '$2b$12$Q06ZkJmYlfr9tANm23xsQegxCI7gJ3hvqEYF402QV1/IbQPVRmF.q' WHERE full_name = 'Soham Kumar Dey';
UPDATE users SET password_hash = '$2b$12$NAoBaojpBHnDDoVsOU7WxOAg0.A19qLO.XonxvWWRl41o9mMKTiE.' WHERE full_name = 'Utkarsh Phalphale';
UPDATE users SET password_hash = '$2b$12$rYz63rTAbY9RaZtbMcDmBeh/MWPxntG6Pk7uPHYp3c5IK4mZ55cUe' WHERE full_name = 'Rajat Morya';
UPDATE users SET password_hash = '$2b$12$Fg2MWFBB3YtuL7q3P.LNpeN23MP1DdnYfeAVaKh4XUthNnYbcKsCO' WHERE full_name = 'Ayush Kush';
UPDATE users SET password_hash = '$2b$12$bvN6wY8qP35xOUjvY5r7eOyR/HvUHbqGZgavJMDLnPFQjtZRaZ6IK' WHERE full_name = 'Ninad Soman';
UPDATE users SET password_hash = '$2b$12$jT8TUkmjyk9AUVkl/JH3Z.gQ.zz4ARI61zafC0s7ss4oDoJB5gYvK' WHERE full_name = 'Vivek Yadav';
UPDATE users SET password_hash = '$2b$12$2SJOWPhT5/Vnb.bVri5zj.owJESODRm9pttqxsFD3s.b6JjgltGQK' WHERE full_name = 'Satyam Bavankar';
UPDATE users SET password_hash = '$2b$12$vE3slXnL2jK6Q1ksDTnMFOHR4L/DR6mlPGJdGpTM66mwKtP4nsH/K' WHERE full_name = 'Tanay Mapare';
UPDATE users SET password_hash = '$2b$12$3OFAOPmtDwC8lcabbE4Pl.r9U2UcmKhbHFc1UkC9H71sRZGVuDsdu' WHERE full_name = 'Ayush Shrivastava';
UPDATE users SET password_hash = '$2b$12$kk1jKbnmgypn9n9sqpGRA.0rLDAVNVIJdnft.27FDkdXO7LvCbVlK' WHERE full_name = 'Ansh Mittal';
UPDATE users SET password_hash = '$2b$12$0uRzjm9Dqs1lWxV6KADdY.BjXsOjmpozTWjknjuP11dG7QR7XKV4i' WHERE full_name = 'Rajdeep Kala';
UPDATE users SET password_hash = '$2b$12$9sfh5B9c955pP.mIF.I4rey3YS90BI9X4lJUft4gd2fo.W2GwGRT.' WHERE full_name = 'Satyendra Rathore';
UPDATE users SET password_hash = '$2b$12$.Zpu.jmTtftHujWUm3Uz8OahR.zcyAAkSx7ly9eX0Yg1andvcvuQO' WHERE full_name = 'Prabal Singh';
UPDATE users SET password_hash = '$2b$12$hs3jFd3N7kVpsbyEh1X4B.HZ6kn9igf1dhqoVkCGu2WBTbLPyStdu' WHERE full_name = 'Akash Dwivedi';
UPDATE users SET password_hash = '$2b$12$ynVShBrM.Q14m5rUxTUvZe4K.447LOtP/SsxPA/u4jgCB9dR9hPo2' WHERE full_name = 'Priyanshu Raj';
UPDATE users SET password_hash = '$2b$12$Pq2toaUdNkynpBZIbp2hHuouNjNnd4.CE.GyzjOxSw5GqpIPrTy6i' WHERE full_name = 'Atul Pawar';
UPDATE users SET password_hash = '$2b$12$6oFcaklOmSEA0nGBlmfVZ.gMDSR6BBG25ThidHRhmR4Fz6.DidcP2' WHERE full_name = 'Himanshu Muleva';
UPDATE users SET password_hash = '$2b$12$ejOMoTww67/e23fQSuykpuCsTSPv4SUSEJvseSVNZ/x88sZ/Mh/HG' WHERE full_name = 'Anuj Sharma';
UPDATE users SET password_hash = '$2b$12$HflPyYYZUJ4EdpAmkTpuMeo/ARYcm/Rz8X9gTUijSlllBFDReZFaS' WHERE full_name = 'Akash Rout';
UPDATE users SET password_hash = '$2b$12$MgVi3TCXSeJOz6boL36LZ.iGN46xc62TLbULROMPhtg6q9Om2ZW4W' WHERE full_name = 'Pratinav';
UPDATE users SET password_hash = '$2b$12$oo9d5yI/rPivkgxB8cbdZuhtbhWv8EiXeuvGr4lx0i9QZ1PJYuvXi' WHERE full_name = 'Satyabrat Panigrahi';
UPDATE users SET password_hash = '$2b$12$aH2tuukdyT095.ypQHQ./enzLDaAQk8.fFKLlPO8CRTzWW4OvFF0C' WHERE full_name = 'Rohan Sinha';
UPDATE users SET password_hash = '$2b$12$KSjdlb7eYpMaCccTKMAvAe5M.XOsBBDyIgkPkhAymLKCCnJrK3FHO' WHERE full_name = 'Nishant Vij';
UPDATE users SET password_hash = '$2b$12$DzKISrTEdow4H2E80zetduTf0/eNBrL0uWLhE21dzflRN32e1BJ0C' WHERE full_name = 'Pritish Bhatiya';
UPDATE users SET password_hash = '$2b$12$278wWG.vBPSJYV2XzkB/9uJesS0dQ49rPTyzDWW5DvYYvIklzwz5m' WHERE full_name = 'Apoorv Singh';
UPDATE users SET password_hash = '$2b$12$xut3Mwp7jUla9iEIoBPs3u/fGfVVZPPEZ.EYfgWhdaV84Mj1g1jsu' WHERE full_name = 'Pranav Tiwari';
UPDATE users SET password_hash = '$2b$12$paHEXIlzDff6HcrmUeyhCOEyvAAuBxy1wms4gfR.2MosjOSjp0tAO' WHERE full_name = 'Abhijeet Singh';

SET SQL_SAFE_UPDATES = 1;


-- ============================================
-- NEW USERS FROM DATATABLE.MD
-- ============================================

-- INSERT DUMMY AADHAR RECORDS
INSERT INTO dummy_aadhar_records (aadhar_number, full_name, date_of_birth, address) VALUES
('770000000000', 'Aakarsha Jobi', '2000-01-01', 'Ernakulam City'),
('770000000001', 'Aakash Gangwar', '2000-01-01', 'Meerut City'),
('770000000002', 'Aarti Kumari', '2000-01-01', 'Patna City'),
('770000000003', 'Aashish Malik', '2000-01-01', 'Gurgaon City'),
('770000000004', 'Aayush Singla', '2000-01-01', 'Ludhiana City'),
('770000000005', 'Abhijit Mandloi', '2000-01-01', 'Indore City'),
('770000000006', 'Abhishek Potbhare', '2000-01-01', 'Nagpur City'),
('770000000007', 'Abhishek Singh', '2000-01-01', 'Lucknow City'),
('770000000008', 'Abhishek Verma', '2000-01-01', 'Varanasi City'),
('770000000009', 'Adarsh Kamble', '2000-01-01', 'Pune City'),
('770000000010', 'Adarsh Singh', '2000-01-01', 'Kanpur City'),
('770000000011', 'Adhyyan Chaturvedi', '2000-01-01', 'Lucknow City'),
('770000000012', 'Aditya Bhosale', '2000-01-01', 'Mumbai City'),
('770000000013', 'Aditya Singh', '2000-01-01', 'Allahabad City'),
('770000000014', 'Aditya Ningadalli', '2000-01-01', 'Belgaum City'),
('770000000015', 'Aditya Shah', '2000-01-01', 'Ahmedabad City'),
('770000000016', 'Adwaidh CR', '2000-01-01', 'Thiruvananthapuram City'),
('770000000017', 'Akash Dwivedi', '2000-01-01', 'Lucknow City'),
('770000000018', 'Akhil Kothari', '2000-01-01', 'Jaipur City'),
('770000000019', 'Akshay Uparikar', '2000-01-01', 'Pune City'),
('770000000020', 'Alok Singh', '2000-01-01', 'Patna City'),
('770000000021', 'Alok Randive', '2000-01-01', 'Nashik City'),
('770000000022', 'Aman', '2000-01-01', 'Delhi City'),
('770000000023', 'Aman Kumar', '2000-01-01', 'Patna City'),
('770000000024', 'Anand Chaubey', '2000-01-01', 'Varanasi City'),
('770000000025', 'Anil Kumar', '2000-01-01', 'Patna City'),
('770000000026', 'Animesh Tyagi', '2000-01-01', 'Meerut City'),
('770000000027', 'Anirudh Deshmukh', '2000-01-01', 'Nagpur City'),
('770000000028', 'Anjali Paramhans', '2000-01-01', 'Pune City'),
('770000000029', 'Ankit Kumar', '2000-01-01', 'Patna City'),
('770000000030', 'Ansh Jain', '2000-01-01', 'Indore City'),
('770000000031', 'Anshul Lodhi', '2000-01-01', 'Bhopal City'),
('770000000032', 'Anubhav Chauhan', '2000-01-01', 'Dehradun City'),
('770000000033', 'Anurag Vinchurkar', '2000-01-01', 'Nagpur City'),
('770000000034', 'Anurag Tiwari', '2000-01-01', 'Lucknow City'),
('770000000035', 'Anushri Sharma', '2000-01-01', 'Jaipur City'),
('770000000036', 'Apoorv Singh', '2000-01-01', 'Lucknow City'),
('770000000037', 'Arya Nanda', '2000-01-01', 'Thiruvananthapuram City'),
('770000000038', 'Ashi Kankane', '2000-01-01', 'Indore City'),
('770000000039', 'Ashish Kothale', '2000-01-01', 'Pune City'),
('770000000040', 'Ashitosh Nandanwar', '2000-01-01', 'Nagpur City'),
('770000000041', 'Avadhut Joshi', '2000-01-01', 'Pune City'),
('770000000042', 'Avu Kundana', '2000-01-01', 'Hyderabad City'),
('770000000043', 'Awinash Kumar', '2000-01-01', 'Patna City'),
('770000000044', 'Ayush Kush', '2000-01-01', 'Patna City'),
('770000000045', 'Ayush Soni', '2000-01-01', 'Indore City'),
('770000000046', 'Ayush Tyagi', '2000-01-01', 'Meerut City'),
('770000000047', 'Ayush Verma', '2000-01-01', 'Lucknow City'),
('770000000048', 'Bala Rao', '2000-01-01', 'Visakhapatnam City'),
('770000000049', 'Balaji P', '2000-01-01', 'Chennai City'),
('770000000050', 'Bhavjeet Singh', '2000-01-01', 'Amritsar City'),
('770000000051', 'Bhavna Agrawal', '2000-01-01', 'Jaipur City'),
('770000000052', 'Bora Reddy', '2000-01-01', 'Hyderabad City'),
('770000000053', 'C Achhaiyya', '2000-01-01', 'Chennai City'),
('770000000054', 'Chaudhari Jitendrakumar', '2000-01-01', 'Surat City'),
('770000000055', 'Chetan Choudhari', '2000-01-01', 'Pune City'),
('770000000056', 'Daksh Shrivastava', '2000-01-01', 'Lucknow City'),
('770000000057', 'Danish Husain', '2000-01-01', 'Lucknow City'),
('770000000058', 'Deepak', '2000-01-01', 'Delhi City'),
('770000000059', 'Deepanshu Tiwari', '2000-01-01', 'Lucknow City'),
('770000000060', 'Deepika Vishwakarma', '2000-01-01', 'Lucknow City'),
('770000000061', 'Devam Prajapati', '2000-01-01', 'Ahmedabad City'),
('770000000062', 'Devansh Choudhary', '2000-01-01', 'Jaipur City'),
('770000000063', 'Devansh Singh', '2000-01-01', 'Lucknow City'),
('770000000064', 'Devashish Ugale', '2000-01-01', 'Mumbai City'),
('770000000065', 'Digambar Rathod', '2000-01-01', 'Pune City'),
('770000000066', 'Diksha Kulkarni', '2000-01-01', 'Pune City'),
('770000000067', 'Emandi Kumar', '2000-01-01', 'Visakhapatnam City'),
('770000000068', 'Gargi Hawaldar', '2000-01-01', 'Pune City'),
('770000000069', 'Gaurav Verma', '2000-01-01', 'Lucknow City'),
('770000000070', 'Gauri Tyagi', '2000-01-01', 'Meerut City'),
('770000000071', 'Gottumukkala Varma', '2000-01-01', 'Vijayawada City'),
('770000000072', 'Gourav Jain', '2000-01-01', 'Jaipur City'),
('770000000073', 'Gulshan Anand', '2000-01-01', 'Delhi City'),
('770000000074', 'Gunupati Reddy', '2000-01-01', 'Hyderabad City'),
('770000000075', 'Hardik', '2000-01-01', 'Ahmedabad City'),
('770000000076', 'Harsh Yadav', '2000-01-01', 'Lucknow City'),
('770000000077', 'Harshada Ghaywat', '2000-01-01', 'Nagpur City'),
('770000000078', 'Harshavardhan Latkar', '2000-01-01', 'Mumbai City'),
('770000000079', 'Harshit Anjana', '2000-01-01', 'Jaipur City'),
('770000000080', 'Ishaan Garg', '2000-01-01', 'Delhi City'),
('770000000081', 'Jay Sharma', '2000-01-01', 'Jaipur City'),
('770000000082', 'Jinesh Chougule', '2000-01-01', 'Kolhapur City'),
('770000000083', 'Jitesh Mehta', '2000-01-01', 'Mumbai City'),
('770000000084', 'Kanik Yadav', '2000-01-01', 'Lucknow City'),
('770000000085', 'Kartikey Tyagi', '2000-01-01', 'Meerut City'),
('770000000086', 'Keval Makdiya', '2000-01-01', 'Surat City'),
('770000000087', 'Kishan Singh', '2000-01-01', 'Jaipur City'),
('770000000088', 'Kolluri Renusree', '2000-01-01', 'Vijayawada City'),
('770000000089', 'Komal Sonawane', '2000-01-01', 'Pune City'),
('770000000090', 'Krishna Panale', '2000-01-01', 'Pune City'),
('770000000091', 'Kusuma Gadi', '2000-01-01', 'Hyderabad City'),
('770000000092', 'Lakshit Chandrakar', '2000-01-01', 'Raipur City'),
('770000000093', 'Lija Sahoo', '2000-01-01', 'Bhubaneswar City'),
('770000000094', 'Maaz Javed', '2000-01-01', 'Lucknow City'),
('770000000095', 'Madhur Chaudhari', '2000-01-01', 'Pune City'),
('770000000096', 'Manohar Dudhat', '2000-01-01', 'Ahmedabad City'),
('770000000097', 'Manoj MN', '2000-01-01', 'Bengaluru City'),
('770000000098', 'Mayank Bansal', '2000-01-01', 'Delhi City'),
('770000000099', 'Mayank Chaudhary', '2000-01-01', 'Jaipur City'),
('770000000100', 'Mayuri Narale', '2000-01-01', 'Pune City'),
('770000000101', 'Md Zaman', '2000-01-01', 'Kolkata City'),
('770000000102', 'Mohammad Durrani', '2000-01-01', 'Lucknow City'),
('770000000103', 'Mourya Konada', '2000-01-01', 'Hyderabad City'),
('770000000104', 'Mousumi Das', '2000-01-01', 'Kolkata City'),
('770000000105', 'Naina Yadav', '2000-01-01', 'Lucknow City'),
('770000000106', 'Nentabaje Sakshi', '2000-01-01', 'Pune City'),
('770000000107', 'Nikita Deshmukh', '2000-01-01', 'Nagpur City'),
('770000000108', 'Nikita Patel', '2000-01-01', 'Ahmedabad City'),
('770000000109', 'Nimisha Verma', '2000-01-01', 'Lucknow City'),
('770000000110', 'Niraj Gehlot', '2000-01-01', 'Jaipur City'),
('770000000111', 'Nirupama Rajana', '2000-01-01', 'Hyderabad City'),
('770000000112', 'Nishant Vij', '2000-01-01', 'Delhi City'),
('770000000113', 'Nishi Ranjan', '2000-01-01', 'Patna City'),
('770000000114', 'Onkar Jogdand', '2000-01-01', 'Pune City'),
('770000000115', 'Palak Sirothiya', '2000-01-01', 'Indore City'),
('770000000116', 'Pittala Soumya', '2000-01-01', 'Vijayawada City'),
('770000000117', 'Prabhat Kumar', '2000-01-01', 'Patna City'),
('770000000118', 'Prajakta Derle', '2000-01-01', 'Pune City'),
('770000000119', 'Prajwal Patil', '2000-01-01', 'Mumbai City'),
('770000000120', 'Prakhar Singh', '2000-01-01', 'Lucknow City'),
('770000000121', 'Pranav Nair', '2000-01-01', 'Kochi City'),
('770000000122', 'Pranavi', '2000-01-01', 'Hyderabad City'),
('770000000123', 'Pranjal Agrawal', '2000-01-01', 'Jaipur City'),
('770000000124', 'Pratiksha Lande', '2000-01-01', 'Pune City'),
('770000000125', 'Priyanshu Patidar', '2000-01-01', 'Indore City'),
('770000000126', 'Priyanshu Raj', '2000-01-01', 'Patna City'),
('770000000127', 'Pruthvi Bhat', '2000-01-01', 'Mangalore City'),
('770000000128', 'Raghavendra Uppar', '2000-01-01', 'Bengaluru City'),
('770000000129', 'Raj Payasi', '2000-01-01', 'Jaipur City'),
('770000000130', 'Rajat Mourya', '2000-01-01', 'Lucknow City'),
('770000000131', 'Rajdeep Kala', '2000-01-01', 'Amritsar City'),
('770000000132', 'Rajnish Singh', '2000-01-01', 'Patna City'),
('770000000133', 'Ram Wandile', '2000-01-01', 'Pune City'),
('770000000134', 'Ratnesh Raja', '2000-01-01', 'Patna City'),
('770000000135', 'Rishi Singh', '2000-01-01', 'Lucknow City'),
('770000000136', 'Ritesh Singh', '2000-01-01', 'Lucknow City'),
('770000000137', 'Ritik Bhattal', '2000-01-01', 'Chandigarh City'),
('770000000138', 'Ronit Singh', '2000-01-01', 'Lucknow City'),
('770000000139', 'Rupesh Patil', '2000-01-01', 'Mumbai City'),
('770000000140', 'Rutuja Khot', '2000-01-01', 'Mumbai City'),
('770000000141', 'S Pranav', '2000-01-01', 'Chennai City'),
('770000000142', 'Sahil Kadam', '2000-01-01', 'Mumbai City'),
('770000000143', 'Saket Bagre', '2000-01-01', 'Indore City'),
('770000000144', 'Saksham Jain', '2000-01-01', 'Jaipur City'),
('770000000145', 'Sakshi Pimpale', '2000-01-01', 'Pune City'),
('770000000146', 'Sanskar Chaurasia', '2000-01-01', 'Lucknow City'),
('770000000147', 'Sarode Mamata', '2000-01-01', 'Pune City'),
('770000000148', 'Sarthak Gore', '2000-01-01', 'Pune City'),
('770000000149', 'Satyam Patel', '2000-01-01', 'Surat City'),
('770000000150', 'Satyendra Rathore', '2000-01-01', 'Jaipur City'),
('770000000151', 'Saurabh Mali', '2000-01-01', 'Pune City'),
('770000000152', 'Sayma Ahmad', '2000-01-01', 'Lucknow City'),
('770000000153', 'Shivansh Mishra', '2000-01-01', 'Lucknow City'),
('770000000154', 'Shreyansh Shah', '2000-01-01', 'Ahmedabad City'),
('770000000155', 'Shristi Mishra', '2000-01-01', 'Lucknow City'),
('770000000156', 'Shubham Dhakate', '2000-01-01', 'Nagpur City'),
('770000000157', 'Shubham Kumar', '2000-01-01', 'Patna City'),
('770000000158', 'Shubham Rahangdale', '2000-01-01', 'Nagpur City'),
('770000000159', 'Shubhra Tiwari', '2000-01-01', 'Lucknow City'),
('770000000160', 'Sonit Nagpure', '2000-01-01', 'Nagpur City'),
('770000000161', 'Soumya Bharti', '2000-01-01', 'Patna City'),
('770000000162', 'Soumya Singh', '2000-01-01', 'Lucknow City'),
('770000000163', 'Sriram S', '2000-01-01', 'Chennai City'),
('770000000164', 'Stalin Kanjiramparambil', '2000-01-01', 'Kochi City'),
('770000000165', 'Sumit Kumar', '2000-01-01', 'Patna City'),
('770000000166', 'Sumit Sharma', '2000-01-01', 'Jaipur City'),
('770000000167', 'Suyesh Dubey', '2000-01-01', 'Lucknow City'),
('770000000168', 'Suyog Tathe', '2000-01-01', 'Pune City'),
('770000000169', 'Swarnil Kokulwar', '2000-01-01', 'Nagpur City'),
('770000000170', 'Swathi Simbothula', '2000-01-01', 'Hyderabad City'),
('770000000171', 'Swati Kumari', '2000-01-01', 'Patna City'),
('770000000172', 'Sweekar Chougale', '2000-01-01', 'Belgaum City'),
('770000000173', 'Tanishq Jarsodiwala', '2000-01-01', 'Mumbai City'),
('770000000174', 'Tanuja Ghaytidak', '2000-01-01', 'Pune City'),
('770000000175', 'Tanvi Gundarwar', '2000-01-01', 'Nagpur City'),
('770000000176', 'Tanya Naidu', '2000-01-01', 'Hyderabad City'),
('770000000177', 'Titiksha Chandrakar', '2000-01-01', 'Raipur City'),
('770000000178', 'Tushar Patil', '2000-01-01', 'Mumbai City'),
('770000000179', 'Ujjwal Chourasia', '2000-01-01', 'Lucknow City'),
('770000000180', 'Vaishnav Uke', '2000-01-01', 'Pune City'),
('770000000181', 'Vansh Dhiman', '2000-01-01', 'Chandigarh City'),
('770000000182', 'Vibhor Jayaswal', '2000-01-01', 'Lucknow City'),
('770000000183', 'Vikhyat Gupta', '2000-01-01', 'Delhi City'),
('770000000184', 'Vishal', '2000-01-01', 'Delhi City'),
('770000000185', 'Vishal Rawat', '2000-01-01', 'Dehradun City'),
('770000000186', 'Yash Dudhe', '2000-01-01', 'Nagpur City'),
('770000000187', 'Yash Mourya', '2000-01-01', 'Lucknow City'),
('770000000188', 'Yash Shirude', '2000-01-01', 'Mumbai City'),
('770000000189', 'Yash Sonkuwar', '2000-01-01', 'Nagpur City');

-- INSERT DUMMY VOTER ID RECORDS
INSERT INTO dummy_voter_id_records (voter_id_number, full_name, date_of_birth) VALUES
('VOT3456789013', 'Aakarsha Jobi', '2000-01-01'),
('VOT3456789014', 'Aakash Gangwar', '2000-01-01'),
('VOT3456789016', 'Aarti Kumari', '2000-01-01'),
('VOT3456789017', 'Aashish Malik', '2000-01-01'),
('VOT3456789018', 'Aayush Singla', '2000-01-01'),
('VOT3456789019', 'Abhijit Mandloi', '2000-01-01'),
('VOT3456789020', 'Abhishek Potbhare', '2000-01-01'),
('VOT3456789021', 'Abhishek Singh', '2000-01-01'),
('VOT3456789022', 'Abhishek Verma', '2000-01-01'),
('VOT3456789023', 'Adarsh Kamble', '2000-01-01'),
('VOT3456789024', 'Adarsh Singh', '2000-01-01'),
('VOT3456789025', 'Adhyyan Chaturvedi', '2000-01-01'),
('VOT3456789026', 'Aditya Bhosale', '2000-01-01'),
('VOT3456789027', 'Aditya Singh', '2000-01-01'),
('VOT3456789028', 'Aditya Ningadalli', '2000-01-01'),
('VOT3456789029', 'Aditya Shah', '2000-01-01'),
('VOT3456789030', 'Adwaidh CR', '2000-01-01'),
('VOT3456789031', 'Akash Dwivedi', '2000-01-01'),
('VOT3456789032', 'Akhil Kothari', '2000-01-01'),
('VOT3456789033', 'Akshay Uparikar', '2000-01-01'),
('VOT3456789034', 'Alok Singh', '2000-01-01'),
('VOT3456789035', 'Alok Randive', '2000-01-01'),
('VOT3456789036', 'Aman', '2000-01-01'),
('VOT3456789037', 'Aman Kumar', '2000-01-01'),
('VOT3456789038', 'Anand Chaubey', '2000-01-01'),
('VOT3456789039', 'Anil Kumar', '2000-01-01'),
('VOT3456789040', 'Animesh Tyagi', '2000-01-01'),
('VOT3456789041', 'Anirudh Deshmukh', '2000-01-01'),
('VOT3456789042', 'Anjali Paramhans', '2000-01-01'),
('VOT3456789043', 'Ankit Kumar', '2000-01-01'),
('VOT3456789044', 'Ansh Jain', '2000-01-01'),
('VOT3456789045', 'Anshul Lodhi', '2000-01-01'),
('VOT3456789046', 'Anubhav Chauhan', '2000-01-01'),
('VOT3456789047', 'Anurag Vinchurkar', '2000-01-01'),
('VOT3456789048', 'Anurag Tiwari', '2000-01-01'),
('VOT3456789049', 'Anushri Sharma', '2000-01-01'),
('VOT3456789050', 'Apoorv Singh', '2000-01-01'),
('VOT3456789051', 'Arya Nanda', '2000-01-01'),
('VOT3456789052', 'Ashi Kankane', '2000-01-01'),
('VOT3456789053', 'Ashish Kothale', '2000-01-01'),
('VOT3456789054', 'Ashitosh Nandanwar', '2000-01-01'),
('VOT3456789055', 'Avadhut Joshi', '2000-01-01'),
('VOT3456789056', 'Avu Kundana', '2000-01-01'),
('VOT3456789057', 'Awinash Kumar', '2000-01-01'),
('VOT3456789058', 'Ayush Kush', '2000-01-01'),
('VOT3456789059', 'Ayush Soni', '2000-01-01'),
('VOT3456789060', 'Ayush Tyagi', '2000-01-01'),
('VOT3456789061', 'Ayush Verma', '2000-01-01'),
('VOT3456789062', 'Bala Rao', '2000-01-01'),
('VOT3456789063', 'Balaji P', '2000-01-01'),
('VOT3456789064', 'Bhavjeet Singh', '2000-01-01'),
('VOT3456789065', 'Bhavna Agrawal', '2000-01-01'),
('VOT3456789066', 'Bora Reddy', '2000-01-01'),
('VOT3456789067', 'C Achhaiyya', '2000-01-01'),
('VOT3456789068', 'Chaudhari Jitendrakumar', '2000-01-01'),
('VOT3456789069', 'Chetan Choudhari', '2000-01-01'),
('VOT3456789070', 'Daksh Shrivastava', '2000-01-01'),
('VOT3456789071', 'Danish Husain', '2000-01-01'),
('VOT3456789072', 'Deepak', '2000-01-01'),
('VOT3456789073', 'Deepanshu Tiwari', '2000-01-01'),
('VOT3456789074', 'Deepika Vishwakarma', '2000-01-01'),
('VOT3456789075', 'Devam Prajapati', '2000-01-01'),
('VOT3456789076', 'Devansh Choudhary', '2000-01-01'),
('VOT3456789077', 'Devansh Singh', '2000-01-01'),
('VOT3456789078', 'Devashish Ugale', '2000-01-01'),
('VOT3456789079', 'Digambar Rathod', '2000-01-01'),
('VOT3456789080', 'Diksha Kulkarni', '2000-01-01'),
('VOT3456789081', 'Emandi Kumar', '2000-01-01'),
('VOT3456789082', 'Gargi Hawaldar', '2000-01-01'),
('VOT3456789083', 'Gaurav Verma', '2000-01-01'),
('VOT3456789084', 'Gauri Tyagi', '2000-01-01'),
('VOT3456789085', 'Gottumukkala Varma', '2000-01-01'),
('VOT3456789086', 'Gourav Jain', '2000-01-01'),
('VOT3456789087', 'Gulshan Anand', '2000-01-01'),
('VOT3456789088', 'Gunupati Reddy', '2000-01-01'),
('VOT3456789089', 'Hardik', '2000-01-01'),
('VOT3456789090', 'Harsh Yadav', '2000-01-01'),
('VOT3456789091', 'Harshada Ghaywat', '2000-01-01'),
('VOT3456789092', 'Harshavardhan Latkar', '2000-01-01'),
('VOT3456789093', 'Harshit Anjana', '2000-01-01'),
('VOT3456789094', 'Ishaan Garg', '2000-01-01'),
('VOT3456789095', 'Jay Sharma', '2000-01-01'),
('VOT3456789096', 'Jinesh Chougule', '2000-01-01'),
('VOT3456789097', 'Jitesh Mehta', '2000-01-01'),
('VOT3456789098', 'Kanik Yadav', '2000-01-01'),
('VOT3456789099', 'Kartikey Tyagi', '2000-01-01'),
('VOT3456789100', 'Keval Makdiya', '2000-01-01'),
('VOT3456789101', 'Kishan Singh', '2000-01-01'),
('VOT3456789102', 'Kolluri Renusree', '2000-01-01'),
('VOT3456789103', 'Komal Sonawane', '2000-01-01'),
('VOT3456789104', 'Krishna Panale', '2000-01-01'),
('VOT3456789105', 'Kusuma Gadi', '2000-01-01'),
('VOT3456789106', 'Lakshit Chandrakar', '2000-01-01'),
('VOT3456789107', 'Lija Sahoo', '2000-01-01'),
('VOT3456789108', 'Maaz Javed', '2000-01-01'),
('VOT3456789109', 'Madhur Chaudhari', '2000-01-01'),
('VOT3456789110', 'Manohar Dudhat', '2000-01-01'),
('VOT3456789111', 'Manoj MN', '2000-01-01'),
('VOT3456789112', 'Mayank Bansal', '2000-01-01'),
('VOT3456789113', 'Mayank Chaudhary', '2000-01-01'),
('VOT3456789114', 'Mayuri Narale', '2000-01-01'),
('VOT3456789115', 'Md Zaman', '2000-01-01'),
('VOT3456789116', 'Mohammad Durrani', '2000-01-01'),
('VOT3456789117', 'Mourya Konada', '2000-01-01'),
('VOT3456789118', 'Mousumi Das', '2000-01-01'),
('VOT3456789119', 'Naina Yadav', '2000-01-01'),
('VOT3456789120', 'Nentabaje Sakshi', '2000-01-01'),
('VOT3456789121', 'Nikita Deshmukh', '2000-01-01'),
('VOT3456789122', 'Nikita Patel', '2000-01-01'),
('VOT3456789123', 'Nimisha Verma', '2000-01-01'),
('VOT3456789124', 'Niraj Gehlot', '2000-01-01'),
('VOT3456789125', 'Nirupama Rajana', '2000-01-01'),
('VOT3456789126', 'Nishant Vij', '2000-01-01'),
('VOT3456789127', 'Nishi Ranjan', '2000-01-01'),
('VOT3456789128', 'Onkar Jogdand', '2000-01-01'),
('VOT3456789129', 'Palak Sirothiya', '2000-01-01'),
('VOT3456789130', 'Pittala Soumya', '2000-01-01'),
('VOT3456789131', 'Prabhat Kumar', '2000-01-01'),
('VOT3456789132', 'Prajakta Derle', '2000-01-01'),
('VOT3456789133', 'Prajwal Patil', '2000-01-01'),
('VOT3456789134', 'Prakhar Singh', '2000-01-01'),
('VOT3456789135', 'Pranav Nair', '2000-01-01'),
('VOT3456789137', 'Pranavi', '2000-01-01'),
('VOT3456789138', 'Pranjal Agrawal', '2000-01-01'),
('VOT3456789139', 'Pratiksha Lande', '2000-01-01'),
('VOT3456789140', 'Priyanshu Patidar', '2000-01-01'),
('VOT3456789141', 'Priyanshu Raj', '2000-01-01'),
('VOT3456789142', 'Pruthvi Bhat', '2000-01-01'),
('VOT3456789143', 'Raghavendra Uppar', '2000-01-01'),
('VOT3456789144', 'Raj Payasi', '2000-01-01'),
('VOT3456789145', 'Rajat Mourya', '2000-01-01'),
('VOT3456789146', 'Rajdeep Kala', '2000-01-01'),
('VOT3456789147', 'Rajnish Singh', '2000-01-01'),
('VOT3456789148', 'Ram Wandile', '2000-01-01'),
('VOT3456789149', 'Ratnesh Raja', '2000-01-01'),
('VOT3456789151', 'Rishi Singh', '2000-01-01'),
('VOT3456789152', 'Ritesh Singh', '2000-01-01'),
('VOT3456789153', 'Ritik Bhattal', '2000-01-01'),
('VOT3456789154', 'Ronit Singh', '2000-01-01'),
('VOT3456789155', 'Rupesh Patil', '2000-01-01'),
('VOT3456789157', 'Rutuja Khot', '2000-01-01'),
('VOT3456789158', 'S Pranav', '2000-01-01'),
('VOT3456789159', 'Sahil Kadam', '2000-01-01'),
('VOT3456789160', 'Saket Bagre', '2000-01-01'),
('VOT3456789161', 'Saksham Jain', '2000-01-01'),
('VOT3456789162', 'Sakshi Pimpale', '2000-01-01'),
('VOT3456789163', 'Sanskar Chaurasia', '2000-01-01'),
('VOT3456789164', 'Sarode Mamata', '2000-01-01'),
('VOT3456789165', 'Sarthak Gore', '2000-01-01'),
('VOT3456789166', 'Satyam Patel', '2000-01-01'),
('VOT3456789167', 'Satyendra Rathore', '2000-01-01'),
('VOT3456789168', 'Saurabh Mali', '2000-01-01'),
('VOT3456789169', 'Sayma Ahmad', '2000-01-01'),
('VOT3456789170', 'Shivansh Mishra', '2000-01-01'),
('VOT3456789171', 'Shreyansh Shah', '2000-01-01'),
('VOT3456789172', 'Shristi Mishra', '2000-01-01'),
('VOT3456789173', 'Shubham Dhakate', '2000-01-01'),
('VOT3456789174', 'Shubham Kumar', '2000-01-01'),
('VOT3456789175', 'Shubham Rahangdale', '2000-01-01'),
('VOT3456789176', 'Shubhra Tiwari', '2000-01-01'),
('VOT3456789178', 'Sonit Nagpure', '2000-01-01'),
('VOT3456789179', 'Soumya Bharti', '2000-01-01'),
('VOT3456789180', 'Soumya Singh', '2000-01-01'),
('VOT3456789181', 'Sriram S', '2000-01-01'),
('VOT3456789182', 'Stalin Kanjiramparambil', '2000-01-01'),
('VOT3456789183', 'Sumit Kumar', '2000-01-01'),
('VOT3456789184', 'Sumit Sharma', '2000-01-01'),
('VOT3456789185', 'Suyesh Dubey', '2000-01-01'),
('VOT3456789186', 'Suyog Tathe', '2000-01-01'),
('VOT3456789187', 'Swarnil Kokulwar', '2000-01-01'),
('VOT3456789188', 'Swathi Simbothula', '2000-01-01'),
('VOT3456789189', 'Swati Kumari', '2000-01-01'),
('VOT3456789190', 'Sweekar Chougale', '2000-01-01'),
('VOT3456789191', 'Tanishq Jarsodiwala', '2000-01-01'),
('VOT3456789192', 'Tanuja Ghaytidak', '2000-01-01'),
('VOT3456789193', 'Tanvi Gundarwar', '2000-01-01'),
('VOT3456789194', 'Tanya Naidu', '2000-01-01'),
('VOT3456789195', 'Titiksha Chandrakar', '2000-01-01'),
('VOT3456789196', 'Tushar Patil', '2000-01-01'),
('VOT3456789197', 'Ujjwal Chourasia', '2000-01-01'),
('VOT3456789199', 'Vaishnav Uke', '2000-01-01'),
('VOT3456789200', 'Vansh Dhiman', '2000-01-01'),
('VOT3456789201', 'Vibhor Jayaswal', '2000-01-01'),
('VOT3456789202', 'Vikhyat Gupta', '2000-01-01'),
('VOT3456789203', 'Vishal', '2000-01-01'),
('VOT3456789204', 'Vishal Rawat', '2000-01-01'),
('VOT3456789206', 'Yash Dudhe', '2000-01-01'),
('VOT3456789207', 'Yash Mourya', '2000-01-01'),
('VOT3456789208', 'Yash Shirude', '2000-01-01'),
('VOT3456789209', 'Yash Sonkuwar', '2000-01-01');

-- INSERT USERS
INSERT INTO users (email, password_hash, full_name, phone_number, date_of_birth, gender, address, city, state, pincode, aadhar_number, voter_id_number, is_active, is_verified, approved_at) VALUES
('aakarsha.jobi0@email.com', '$2b$12$O7.wVh6wEKfMqbHJfqxND.1/Pu9NDu4obnujj./TDJqa0.TXm/5oG', 'Aakarsha Jobi', '7000000000', '2000-01-01', 'MALE', 'Ernakulam City', 'Ernakulam', 'Kerala', '000000', '770000000000', 'VOT3456789013', TRUE, TRUE, NOW()),
('aakash.gangwar1@email.com', '$2b$12$47VepK8EJfEs5MyXTGem2u8evGu.zBugY6UJgoyX/ex9GWcN7ufla', 'Aakash Gangwar', '7000000001', '2000-01-01', 'MALE', 'Meerut City', 'Meerut', 'Uttar Pradesh', '000000', '770000000001', 'VOT3456789014', TRUE, TRUE, NOW()),
('aarti.kumari2@email.com', '$2b$12$P5PMTlZ/blcKP0.lJLCiN.jg.YJ3/8c/Nm0EQTgEOBtyLnPIQuPgG', 'Aarti Kumari', '7000000002', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000002', 'VOT3456789016', TRUE, TRUE, NOW()),
('aashish.malik3@email.com', '$2b$12$lnyedM7ZEgI2uZ8pQVZmoeqRU5ft38Cxm7CqTPMxM7v6h2YRnNOAO', 'Aashish Malik', '7000000003', '2000-01-01', 'MALE', 'Gurgaon City', 'Gurgaon', 'Haryana', '000000', '770000000003', 'VOT3456789017', TRUE, TRUE, NOW()),
('aayush.singla4@email.com', '$2b$12$1OyVJrg3rcEYF/8MTK0Ibe/qelTR/UPDboK20JUAW.zkYCyROw2P6', 'Aayush Singla', '7000000004', '2000-01-01', 'MALE', 'Ludhiana City', 'Ludhiana', 'Punjab', '000000', '770000000004', 'VOT3456789018', TRUE, TRUE, NOW()),
('abhijit.mandloi5@email.com', '$2b$12$Pll2N4UsRY7Nn4DmI02xbucmc9hZqdfm42JZuMrXEMuTjVIV1js1S', 'Abhijit Mandloi', '7000000005', '2000-01-01', 'MALE', 'Indore City', 'Indore', 'Madhya Pradesh', '000000', '770000000005', 'VOT3456789019', TRUE, TRUE, NOW()),
('abhishek.potbhare6@email.com', '$2b$12$kGIv.5/I6VkOpwnAW1/RK.kz3f2GwGeTv7VvDbQyFULyTYAAQbbDW', 'Abhishek Potbhare', '7000000006', '2000-01-01', 'MALE', 'Nagpur City', 'Nagpur', 'Maharashtra', '000000', '770000000006', 'VOT3456789020', TRUE, TRUE, NOW()),
('abhishek.singh7@email.com', '$2b$12$nxhBZzllJTOX97us66WIT.nK/lUGIBo7bo0qlzj0yUvezRkig.LYu', 'Abhishek Singh', '7000000007', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000007', 'VOT3456789021', TRUE, TRUE, NOW()),
('abhishek.verma8@email.com', '$2b$12$q6beFfWQZuEZJq75ddeMsufYy.B6eVwhup3shF.SFBOG7wqdn4mxS', 'Abhishek Verma', '7000000008', '2000-01-01', 'MALE', 'Varanasi City', 'Varanasi', 'Uttar Pradesh', '000000', '770000000008', 'VOT3456789022', TRUE, TRUE, NOW()),
('adarsh.kamble9@email.com', '$2b$12$rVQXu/YLOEtOj9baUhTMTe0bV7gzK92DekGxrRNp7P5Jpw0qd3yVa', 'Adarsh Kamble', '7000000009', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000009', 'VOT3456789023', TRUE, TRUE, NOW()),
('adarsh.singh10@email.com', '$2b$12$eYxbOa/LJS22Ajj/tLwKqOvy8B4BXS3u7Qd7SOLfh6/LUo6q9MCHG', 'Adarsh Singh', '7000000010', '2000-01-01', 'MALE', 'Kanpur City', 'Kanpur', 'Uttar Pradesh', '000000', '770000000010', 'VOT3456789024', TRUE, TRUE, NOW()),
('adhyyan.chaturvedi11@email.com', '$2b$12$gM8Sk4XSl1MPrMDarFDcG..rw6Lhie3oDQ/.nKIkzbhXhR6COM6yG', 'Adhyyan Chaturvedi', '7000000011', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000011', 'VOT3456789025', TRUE, TRUE, NOW()),
('aditya.bhosale12@email.com', '$2b$12$HS8hdYuj7N5ojSmJcVWPrOE00oEeoi5ZKhnU8SKksoIityOXSzP.K', 'Aditya Bhosale', '7000000012', '2000-01-01', 'MALE', 'Mumbai City', 'Mumbai', 'Maharashtra', '000000', '770000000012', 'VOT3456789026', TRUE, TRUE, NOW()),
('aditya.singh13@email.com', '$2b$12$KxBc3gesrwzf.mgfPpWNCe8yHkPvQx7C3UGz5VjspjCLp1xQr78RG', 'Aditya Singh', '7000000013', '2000-01-01', 'MALE', 'Allahabad City', 'Allahabad', 'Uttar Pradesh', '000000', '770000000013', 'VOT3456789027', TRUE, TRUE, NOW()),
('aditya.ningadalli14@email.com', '$2b$12$ButGLxr2L1JAcjPHYx6oX.iLNYT1zV1Rf9ICjfaDKE1stRi61FcRS', 'Aditya Ningadalli', '7000000014', '2000-01-01', 'MALE', 'Belgaum City', 'Belgaum', 'Karnataka', '000000', '770000000014', 'VOT3456789028', TRUE, TRUE, NOW()),
('aditya.shah15@email.com', '$2b$12$5hsTXw/O4BQ2HMXEO24biOEgE6/U7R.62iVtJLw4XSmMBL02I15hW', 'Aditya Shah', '7000000015', '2000-01-01', 'MALE', 'Ahmedabad City', 'Ahmedabad', 'Gujarat', '000000', '770000000015', 'VOT3456789029', TRUE, TRUE, NOW()),
('adwaidh.cr16@email.com', '$2b$12$gaa9rDK80Gp887JFl7/FH.iiq9Z/a0Kejh4Cw98JijlkfAqZRmLl6', 'Adwaidh CR', '7000000016', '2000-01-01', 'MALE', 'Thiruvananthapuram City', 'Thiruvananthapuram', 'Kerala', '000000', '770000000016', 'VOT3456789030', TRUE, TRUE, NOW()),
('akash.dwivedi17@email.com', '$2b$12$5XjiA7WKQHGERsB9oWQLKOPGPfWEodh3GOAiWOcguajDf3eyI7E2W', 'Akash Dwivedi', '7000000017', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000017', 'VOT3456789031', TRUE, TRUE, NOW()),
('akhil.kothari18@email.com', '$2b$12$h32SMa9S8vj/LQYiA6xurOEGthn.T6VB/8ebu8eFMZ66mR6FzEdYe', 'Akhil Kothari', '7000000018', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000018', 'VOT3456789032', TRUE, TRUE, NOW()),
('akshay.uparikar19@email.com', '$2b$12$ncbBpF9nzoWgilPtH52PIeEmb4niqZ/.qGz067fzPVw9kjGoY23HW', 'Akshay Uparikar', '7000000019', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000019', 'VOT3456789033', TRUE, TRUE, NOW()),
('alok.singh20@email.com', '$2b$12$xDPiL3F56hmgydtCZVUk5eVyJc.bG6Q37l6AvKg3Vqx.BQ0nXmrjG', 'Alok Singh', '7000000020', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000020', 'VOT3456789034', TRUE, TRUE, NOW()),
('alok.randive21@email.com', '$2b$12$fWemir1xpIR9JukeUOnNt.M3Mh/TpAXXq84kIXe3Sno4vGo6wHp7G', 'Alok Randive', '7000000021', '2000-01-01', 'MALE', 'Nashik City', 'Nashik', 'Maharashtra', '000000', '770000000021', 'VOT3456789035', TRUE, TRUE, NOW()),
('aman22@email.com', '$2b$12$JhmOdliuPM/PFeRxHyF.k..U8eD8/hAvVPhwF.eiQXYoK.IL0x3Ou', 'Aman', '7000000022', '2000-01-01', 'MALE', 'Delhi City', 'Delhi', 'Delhi', '000000', '770000000022', 'VOT3456789036', TRUE, TRUE, NOW()),
('aman.kumar23@email.com', '$2b$12$8uHZ.xTxIVcBtf1jMQOk4O2QnYP7rTOResmnivHSIQ1qJXYY8OoWy', 'Aman Kumar', '7000000023', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000023', 'VOT3456789037', TRUE, TRUE, NOW()),
('anand.chaubey24@email.com', '$2b$12$4SMEH4ItsPqsuJs7/QeTB.0uY/A91gvWOO2qorD.38KtAptDTLGI6', 'Anand Chaubey', '7000000024', '2000-01-01', 'MALE', 'Varanasi City', 'Varanasi', 'Uttar Pradesh', '000000', '770000000024', 'VOT3456789038', TRUE, TRUE, NOW()),
('anil.kumar25@email.com', '$2b$12$gLxC6r8yB4xupcaH6bZRMexcBHgE/U6oV8h6riDe07gYrRNL3.TXe', 'Anil Kumar', '7000000025', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000025', 'VOT3456789039', TRUE, TRUE, NOW()),
('animesh.tyagi26@email.com', '$2b$12$9ZNTZPBtKKlXxpedDwK1V.AyXNRZ3DBMKnvr52YrW9OB6t9H2oRdS', 'Animesh Tyagi', '7000000026', '2000-01-01', 'MALE', 'Meerut City', 'Meerut', 'Uttar Pradesh', '000000', '770000000026', 'VOT3456789040', TRUE, TRUE, NOW()),
('anirudh.deshmukh27@email.com', '$2b$12$aVDdYn3/vSbYqp2IyuibhuPhCbeR9aWCy/Uz/RMPEvs/U7XJ1DzhW', 'Anirudh Deshmukh', '7000000027', '2000-01-01', 'MALE', 'Nagpur City', 'Nagpur', 'Maharashtra', '000000', '770000000027', 'VOT3456789041', TRUE, TRUE, NOW()),
('anjali.paramhans28@email.com', '$2b$12$qwQzLQ3cU142jZ1SHybOzOzELBc6gAuiKcy64tDt.Ta/lzUtqYpsC', 'Anjali Paramhans', '7000000028', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000028', 'VOT3456789042', TRUE, TRUE, NOW()),
('ankit.kumar29@email.com', '$2b$12$fE1YAZLjhHDrFzpWOQYcWuEpJ4ctpnr56naUH.OkY0QWgibo8IXYu', 'Ankit Kumar', '7000000029', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000029', 'VOT3456789043', TRUE, TRUE, NOW()),
('ansh.jain30@email.com', '$2b$12$LyzzgISRjs0h1b9m19r34uGYhHtakF2/ek7zrm.4A2r3viOBxkzjC', 'Ansh Jain', '7000000030', '2000-01-01', 'MALE', 'Indore City', 'Indore', 'Madhya Pradesh', '000000', '770000000030', 'VOT3456789044', TRUE, TRUE, NOW()),
('anshul.lodhi31@email.com', '$2b$12$jb.W/5cyZU3d48bzbT9DtORf3NQbj0Yj5i1a4UexywNKj7e5qfv2S', 'Anshul Lodhi', '7000000031', '2000-01-01', 'MALE', 'Bhopal City', 'Bhopal', 'Madhya Pradesh', '000000', '770000000031', 'VOT3456789045', TRUE, TRUE, NOW()),
('anubhav.chauhan32@email.com', '$2b$12$0Yu5h.MYNLwsRNN3f1N1JOht7Dcqyht6kp7EfaO9QWwoxg7MINJ3W', 'Anubhav Chauhan', '7000000032', '2000-01-01', 'MALE', 'Dehradun City', 'Dehradun', 'Uttarakhand', '000000', '770000000032', 'VOT3456789046', TRUE, TRUE, NOW()),
('anurag.vinchurkar33@email.com', '$2b$12$f301J7wxitwfeYYjrWAY0.QIA2i.284TO9QNOgUtnedg9GoNpvzGq', 'Anurag Vinchurkar', '7000000033', '2000-01-01', 'MALE', 'Nagpur City', 'Nagpur', 'Maharashtra', '000000', '770000000033', 'VOT3456789047', TRUE, TRUE, NOW()),
('anurag.tiwari34@email.com', '$2b$12$hlQThmerI9jMkLEg7svfpuAyuBM7Uqj6TvwMyF1Ekn0TBLUuvw4.2', 'Anurag Tiwari', '7000000034', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000034', 'VOT3456789048', TRUE, TRUE, NOW()),
('anushri.sharma35@email.com', '$2b$12$cUOlekCX4599w3c0w9x4G.l7Xr/AFdtEvscMXZGGjicNADCg/bNYu', 'Anushri Sharma', '7000000035', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000035', 'VOT3456789049', TRUE, TRUE, NOW()),
('apoorv.singh36@email.com', '$2b$12$PZlnGNK6LgWFu4Nf7EJzRu5gA11/L6HfXbv9OS4HNUDgmLKUk9wVS', 'Apoorv Singh', '7000000036', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000036', 'VOT3456789050', TRUE, TRUE, NOW()),
('arya.nanda37@email.com', '$2b$12$1ImqiLrKtnVOtES58zB54.wGxCjZjyVfb3EyslRGeVNmvvqIN2bg2', 'Arya Nanda', '7000000037', '2000-01-01', 'MALE', 'Thiruvananthapuram City', 'Thiruvananthapuram', 'Kerala', '000000', '770000000037', 'VOT3456789051', TRUE, TRUE, NOW()),
('ashi.kankane38@email.com', '$2b$12$BL.n2F5Ai.bDis0CBoW5HeBIFMM2naipSmY/9K04kojL8OHJiPkNW', 'Ashi Kankane', '7000000038', '2000-01-01', 'MALE', 'Indore City', 'Indore', 'Madhya Pradesh', '000000', '770000000038', 'VOT3456789052', TRUE, TRUE, NOW()),
('ashish.kothale39@email.com', '$2b$12$FCKhxeSsC9wPgQANeR6vBObE1Mj2cxYb7LgjjYeKQTW55riqmHkve', 'Ashish Kothale', '7000000039', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000039', 'VOT3456789053', TRUE, TRUE, NOW()),
('ashitosh.nandanwar40@email.com', '$2b$12$dD/CgN1vuWCcNrZcqt1DjeWaRQHlwnXDZvAjll4qnSun/PAy700c2', 'Ashitosh Nandanwar', '7000000040', '2000-01-01', 'MALE', 'Nagpur City', 'Nagpur', 'Maharashtra', '000000', '770000000040', 'VOT3456789054', TRUE, TRUE, NOW()),
('avadhut.joshi41@email.com', '$2b$12$eB3oL0wh5J6iQKFtjX8W3ONK3VE3MidAPwUOBi9d4UKnoIFhjz6W6', 'Avadhut Joshi', '7000000041', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000041', 'VOT3456789055', TRUE, TRUE, NOW()),
('avu.kundana42@email.com', '$2b$12$L8jvoSPOVC9kIlcHBbMDhOcHkT.h5mnV1xpaRBhKl8SyBWDYmQdtW', 'Avu Kundana', '7000000042', '2000-01-01', 'MALE', 'Hyderabad City', 'Hyderabad', 'Telangana', '000000', '770000000042', 'VOT3456789056', TRUE, TRUE, NOW()),
('awinash.kumar43@email.com', '$2b$12$9LkeLxNrIqs6m7uoTa4aOOeCOhnpXIlVfwuJxXMKMLyAwW2yr0Bj.', 'Awinash Kumar', '7000000043', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000043', 'VOT3456789057', TRUE, TRUE, NOW()),
('ayush.kush44@email.com', '$2b$12$TfsvPjv9qswudnoITlndmOPqVwoktYen3CHQD8cCnHjfowkiajDs6', 'Ayush Kush', '7000000044', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000044', 'VOT3456789058', TRUE, TRUE, NOW()),
('ayush.soni45@email.com', '$2b$12$7WKuKA80M2PK.ykOPDwsoe1ovzQR6VNHEQIe8rHgu02JaJqzg/bvG', 'Ayush Soni', '7000000045', '2000-01-01', 'MALE', 'Indore City', 'Indore', 'Madhya Pradesh', '000000', '770000000045', 'VOT3456789059', TRUE, TRUE, NOW()),
('ayush.tyagi46@email.com', '$2b$12$RsA7qEgsy353VelSJaAjz.jDMxd6NK3J1txsRA0YRYUOrKMLlnOeO', 'Ayush Tyagi', '7000000046', '2000-01-01', 'MALE', 'Meerut City', 'Meerut', 'Uttar Pradesh', '000000', '770000000046', 'VOT3456789060', TRUE, TRUE, NOW()),
('ayush.verma47@email.com', '$2b$12$DpyJ5F6c6HB3lcL.3H4Xfu//Omwdzo/lHH7UDuIMD9aPj/ozvlkmO', 'Ayush Verma', '7000000047', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000047', 'VOT3456789061', TRUE, TRUE, NOW()),
('bala.rao48@email.com', '$2b$12$YWwdxmECybzB2GlTTtSd/eoIzfeIFC3qcI3g4Tch8x5ZEfcdrY8KG', 'Bala Rao', '7000000048', '2000-01-01', 'MALE', 'Visakhapatnam City', 'Visakhapatnam', 'Andhra Pradesh', '000000', '770000000048', 'VOT3456789062', TRUE, TRUE, NOW()),
('balaji.p49@email.com', '$2b$12$TD0cwLFTTPMELXXLRFAY8eo.1p3bBXH2QjZkaVJkv8gD/5vMZ/QRy', 'Balaji P', '7000000049', '2000-01-01', 'MALE', 'Chennai City', 'Chennai', 'Tamil Nadu', '000000', '770000000049', 'VOT3456789063', TRUE, TRUE, NOW()),
('bhavjeet.singh50@email.com', '$2b$12$H1QRHqf6hv73NIU8q2dlDeHXa3wCpCjjMXjz5whn.PTaUL/whF7v6', 'Bhavjeet Singh', '7000000050', '2000-01-01', 'MALE', 'Amritsar City', 'Amritsar', 'Punjab', '000000', '770000000050', 'VOT3456789064', TRUE, TRUE, NOW()),
('bhavna.agrawal51@email.com', '$2b$12$ajVVRVejv0swSQc/bfXvsedYcLlKu5FQqRf5He166Ihm5hJsCjd7e', 'Bhavna Agrawal', '7000000051', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000051', 'VOT3456789065', TRUE, TRUE, NOW()),
('bora.reddy52@email.com', '$2b$12$S6/d7y8ZIEZN3gFWFLv/5ejOTjV7j64yAeZblbWXH6fn/AANRLI06', 'Bora Reddy', '7000000052', '2000-01-01', 'MALE', 'Hyderabad City', 'Hyderabad', 'Telangana', '000000', '770000000052', 'VOT3456789066', TRUE, TRUE, NOW()),
('c.achhaiyya53@email.com', '$2b$12$8DH8irjxSJS/hHsTT6Afp.E/k.5h6jWmqb5rLdG/M0k0XJs7NnIDW', 'C Achhaiyya', '7000000053', '2000-01-01', 'MALE', 'Chennai City', 'Chennai', 'Tamil Nadu', '000000', '770000000053', 'VOT3456789067', TRUE, TRUE, NOW()),
('chaudhari.jitendrakumar54@email.com', '$2b$12$MegIowsGwCtfylzA.CuPZOnZQw1I2O7oYMkR1Rh7ZLRQ40vP4pAaa', 'Chaudhari Jitendrakumar', '7000000054', '2000-01-01', 'MALE', 'Surat City', 'Surat', 'Gujarat', '000000', '770000000054', 'VOT3456789068', TRUE, TRUE, NOW()),
('chetan.choudhari55@email.com', '$2b$12$TqAG4jraw3qh6S2NQv2kW.BvAhZxDqlcny.mxrCcuATgOVRvjCuxq', 'Chetan Choudhari', '7000000055', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000055', 'VOT3456789069', TRUE, TRUE, NOW()),
('daksh.shrivastava56@email.com', '$2b$12$p7RKkA0uCna65EO5mShgIOUqz3pQ40m/8R5HsWqC0ZOsQvlwRx3/W', 'Daksh Shrivastava', '7000000056', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000056', 'VOT3456789070', TRUE, TRUE, NOW()),
('danish.husain57@email.com', '$2b$12$EI0frBI2O8eqDS6rVP9IHOyO/i8uSokMIs/fnrOLNMBZl6vxh4sIm', 'Danish Husain', '7000000057', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000057', 'VOT3456789071', TRUE, TRUE, NOW()),
('deepak58@email.com', '$2b$12$8GlaNv8FYU/biSOgHwmHXuPcJG7lb98.07SaqWgK/h7qGAcw4K.9y', 'Deepak', '7000000058', '2000-01-01', 'MALE', 'Delhi City', 'Delhi', 'Delhi', '000000', '770000000058', 'VOT3456789072', TRUE, TRUE, NOW()),
('deepanshu.tiwari59@email.com', '$2b$12$qTTpaEE.HXAaiYHiT4laQeo6GTqK/zI0l/Wqy1e8QBwVcBeu8tkL2', 'Deepanshu Tiwari', '7000000059', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000059', 'VOT3456789073', TRUE, TRUE, NOW()),
('deepika.vishwakarma60@email.com', '$2b$12$X7lYoyWLtjMCfR.siiL3MeywpsXM4DITQvwjzWXqOwhumUHihFv7S', 'Deepika Vishwakarma', '7000000060', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000060', 'VOT3456789074', TRUE, TRUE, NOW()),
('devam.prajapati61@email.com', '$2b$12$39izwms2DdQrplqKVlGRyuTmIT9eoqYRHRj5.QplyS2INDVEhYb3u', 'Devam Prajapati', '7000000061', '2000-01-01', 'MALE', 'Ahmedabad City', 'Ahmedabad', 'Gujarat', '000000', '770000000061', 'VOT3456789075', TRUE, TRUE, NOW()),
('devansh.choudhary62@email.com', '$2b$12$ZfhQSpvtuhU3Quta/PmxnOVVhxVCWpIdehvsxGh1qkJg2FpRU2ah.', 'Devansh Choudhary', '7000000062', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000062', 'VOT3456789076', TRUE, TRUE, NOW()),
('devansh.singh63@email.com', '$2b$12$Lzs6Fq5Lne5ds34KeSxJbO39S.vJNTv1glTO9r5gyIc2FY7kKl776', 'Devansh Singh', '7000000063', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000063', 'VOT3456789077', TRUE, TRUE, NOW()),
('devashish.ugale64@email.com', '$2b$12$OYPpMGa46HNPY5G5VQv2wuc/cnQunAyB2R7Yk.xOIqUv35AqhcB12', 'Devashish Ugale', '7000000064', '2000-01-01', 'MALE', 'Mumbai City', 'Mumbai', 'Maharashtra', '000000', '770000000064', 'VOT3456789078', TRUE, TRUE, NOW()),
('digambar.rathod65@email.com', '$2b$12$028YcnedOjkIdSJ9NZHsOegoOJA6vn28bJQBixqdytuXUDOjtg42q', 'Digambar Rathod', '7000000065', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000065', 'VOT3456789079', TRUE, TRUE, NOW()),
('diksha.kulkarni66@email.com', '$2b$12$DM2xrcs27AgM1IhvD1DsiuSVDodvHBDz5PU0o8z6tPtGXe/1uJY7a', 'Diksha Kulkarni', '7000000066', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000066', 'VOT3456789080', TRUE, TRUE, NOW()),
('emandi.kumar67@email.com', '$2b$12$gTTRHROfBvJYMz1YpYYZruex3ajCMx8PujBYfpjx6notXIm0sMBwW', 'Emandi Kumar', '7000000067', '2000-01-01', 'MALE', 'Visakhapatnam City', 'Visakhapatnam', 'Andhra Pradesh', '000000', '770000000067', 'VOT3456789081', TRUE, TRUE, NOW()),
('gargi.hawaldar68@email.com', '$2b$12$cq.B5YUj4BBkx/rpcqYKruHVXuE2AvBHT9dJXWYIJljAEzHnRgaHS', 'Gargi Hawaldar', '7000000068', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000068', 'VOT3456789082', TRUE, TRUE, NOW()),
('gaurav.verma69@email.com', '$2b$12$BD6LEcvozibKiCiXbzQVyeXd9TomsjMKF.BbopN02IuYXm6I7G8R.', 'Gaurav Verma', '7000000069', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000069', 'VOT3456789083', TRUE, TRUE, NOW()),
('gauri.tyagi70@email.com', '$2b$12$oGDtwYOa4Kz4gZY0pI61vuQ5b7Pr2TM6U2JHjMYvz4Wx07HyEp4T6', 'Gauri Tyagi', '7000000070', '2000-01-01', 'MALE', 'Meerut City', 'Meerut', 'Uttar Pradesh', '000000', '770000000070', 'VOT3456789084', TRUE, TRUE, NOW()),
('gottumukkala.varma71@email.com', '$2b$12$JxEeGCkLSJDC1ReC/JsSOu5nDpsnXYqG64Gd3xFjKrTRrx8GBmL1i', 'Gottumukkala Varma', '7000000071', '2000-01-01', 'MALE', 'Vijayawada City', 'Vijayawada', 'Andhra Pradesh', '000000', '770000000071', 'VOT3456789085', TRUE, TRUE, NOW()),
('gourav.jain72@email.com', '$2b$12$SezunK/h7rKPvKMxhg/yNulb6eG/5fx0oqI8bPjRtQXjurUpogima', 'Gourav Jain', '7000000072', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000072', 'VOT3456789086', TRUE, TRUE, NOW()),
('gulshan.anand73@email.com', '$2b$12$6A8ym6xmOocUZgvlXPELEezXzGzR0hj7roHESg/rHOTPfkiEudlPa', 'Gulshan Anand', '7000000073', '2000-01-01', 'MALE', 'Delhi City', 'Delhi', 'Delhi', '000000', '770000000073', 'VOT3456789087', TRUE, TRUE, NOW()),
('gunupati.reddy74@email.com', '$2b$12$0Y.ME9V6FRKfra62Sp5XGeYoNAWJePDBwaTN1rTxDW5an4h/CPIbm', 'Gunupati Reddy', '7000000074', '2000-01-01', 'MALE', 'Hyderabad City', 'Hyderabad', 'Telangana', '000000', '770000000074', 'VOT3456789088', TRUE, TRUE, NOW()),
('hardik75@email.com', '$2b$12$XyjdtwRXBDVP7ruW.YDQzOyz/e8i9Y3cx/ldLkNM0VuGrX5nxTlC6', 'Hardik', '7000000075', '2000-01-01', 'MALE', 'Ahmedabad City', 'Ahmedabad', 'Gujarat', '000000', '770000000075', 'VOT3456789089', TRUE, TRUE, NOW()),
('harsh.yadav76@email.com', '$2b$12$34twvtg7IO9EwAYFmdgYAeRS18kpiynxUq2WD1ET2esALR.KUMfRq', 'Harsh Yadav', '7000000076', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000076', 'VOT3456789090', TRUE, TRUE, NOW()),
('harshada.ghaywat77@email.com', '$2b$12$OU5hTXdHvR.xAtiue7VoZ.L.4cpyHaBGPvTpinKMKsTC2jP91JHdm', 'Harshada Ghaywat', '7000000077', '2000-01-01', 'MALE', 'Nagpur City', 'Nagpur', 'Maharashtra', '000000', '770000000077', 'VOT3456789091', TRUE, TRUE, NOW()),
('harshavardhan.latkar78@email.com', '$2b$12$TIr3bnWWTGgx84m7L2JqVuKUZ.yeClHXIQ7Y9bwKl/6hTaMKGqQKq', 'Harshavardhan Latkar', '7000000078', '2000-01-01', 'MALE', 'Mumbai City', 'Mumbai', 'Maharashtra', '000000', '770000000078', 'VOT3456789092', TRUE, TRUE, NOW()),
('harshit.anjana79@email.com', '$2b$12$WTqcjVcoH32xr2GwtjOE2Ony8K325n2hIJn0yLo664CNX/9RlsTaa', 'Harshit Anjana', '7000000079', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000079', 'VOT3456789093', TRUE, TRUE, NOW()),
('ishaan.garg80@email.com', '$2b$12$GaEncPiL6/O3TWkyFLbOa.vadjQp/M.jdc9rbjxUwAjB56sI.NFaW', 'Ishaan Garg', '7000000080', '2000-01-01', 'MALE', 'Delhi City', 'Delhi', 'Delhi', '000000', '770000000080', 'VOT3456789094', TRUE, TRUE, NOW()),
('jay.sharma81@email.com', '$2b$12$Mdy40AxwfeVY7ygKoECFr..hShn0Fvg5WYuBHW63.4xvQUmbNULWW', 'Jay Sharma', '7000000081', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000081', 'VOT3456789095', TRUE, TRUE, NOW()),
('jinesh.chougule82@email.com', '$2b$12$ws6LkfGbl1at.vsJnJiRE.8VlU1gnycw16NnzifJtZAtOFvh1NP9C', 'Jinesh Chougule', '7000000082', '2000-01-01', 'MALE', 'Kolhapur City', 'Kolhapur', 'Maharashtra', '000000', '770000000082', 'VOT3456789096', TRUE, TRUE, NOW()),
('jitesh.mehta83@email.com', '$2b$12$a4HGjeLIFIWt41fvzWw6/uqRAkl8.ObSBfuyN40jYUcXfqRPQn7Z2', 'Jitesh Mehta', '7000000083', '2000-01-01', 'MALE', 'Mumbai City', 'Mumbai', 'Maharashtra', '000000', '770000000083', 'VOT3456789097', TRUE, TRUE, NOW()),
('kanik.yadav84@email.com', '$2b$12$7Hqjn4DO6y/rNqLIAdR5ruCyxSYLrmafbRSWT/eqAyU4d2z5F.IK2', 'Kanik Yadav', '7000000084', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000084', 'VOT3456789098', TRUE, TRUE, NOW()),
('kartikey.tyagi85@email.com', '$2b$12$HnADWFh/Va6ygl1GXlcsGOKPcE1cA.9oAn9dgnpzCiUFKX6jw60ZW', 'Kartikey Tyagi', '7000000085', '2000-01-01', 'MALE', 'Meerut City', 'Meerut', 'Uttar Pradesh', '000000', '770000000085', 'VOT3456789099', TRUE, TRUE, NOW()),
('keval.makdiya86@email.com', '$2b$12$OerIWtKCeiYAKqtE3inBR.D6K/p5t2mhMfRYUvtsIqwtMaHEUYRiC', 'Keval Makdiya', '7000000086', '2000-01-01', 'MALE', 'Surat City', 'Surat', 'Gujarat', '000000', '770000000086', 'VOT3456789100', TRUE, TRUE, NOW()),
('kishan.singh87@email.com', '$2b$12$Lafh91LYSdjSWCfkuS8brOIEpyqoxhHaqOOxiJ5x.BWq81KWdjOGi', 'Kishan Singh', '7000000087', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000087', 'VOT3456789101', TRUE, TRUE, NOW()),
('kolluri.renusree88@email.com', '$2b$12$Bhv1/zW13t3PTK1IcKMcL.fugC4W2JBL4HIPL76udQ3is9/iQvBWS', 'Kolluri Renusree', '7000000088', '2000-01-01', 'MALE', 'Vijayawada City', 'Vijayawada', 'Andhra Pradesh', '000000', '770000000088', 'VOT3456789102', TRUE, TRUE, NOW()),
('komal.sonawane89@email.com', '$2b$12$NnhGkyBz7/DwFy5P8nb1hePcGKprwe.hiZtLUK6tQz6Kanjn276bO', 'Komal Sonawane', '7000000089', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000089', 'VOT3456789103', TRUE, TRUE, NOW()),
('krishna.panale90@email.com', '$2b$12$0uQIOgL7JjR/EtlxrHSOTu4FdZUU/sLlqXzZpmoHROqfcc3rbN30W', 'Krishna Panale', '7000000090', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000090', 'VOT3456789104', TRUE, TRUE, NOW()),
('kusuma.gadi91@email.com', '$2b$12$D9ACIpqcg0plNinsBWSZze53/SaUpRjFCOHVKpnPqJB28NAwz7jh2', 'Kusuma Gadi', '7000000091', '2000-01-01', 'MALE', 'Hyderabad City', 'Hyderabad', 'Telangana', '000000', '770000000091', 'VOT3456789105', TRUE, TRUE, NOW()),
('lakshit.chandrakar92@email.com', '$2b$12$dbqmP1lTDR1GH8P2UfnoNu/BGlNG0arQbqr7cppXZsJYOHr5MmNcG', 'Lakshit Chandrakar', '7000000092', '2000-01-01', 'MALE', 'Raipur City', 'Raipur', 'Chhattisgarh', '000000', '770000000092', 'VOT3456789106', TRUE, TRUE, NOW()),
('lija.sahoo93@email.com', '$2b$12$jaibqAQPww5caAeQkKSfreGZ52L.qfICDt/4ZdchZGPnIb9kLMfT6', 'Lija Sahoo', '7000000093', '2000-01-01', 'MALE', 'Bhubaneswar City', 'Bhubaneswar', 'Odisha', '000000', '770000000093', 'VOT3456789107', TRUE, TRUE, NOW()),
('maaz.javed94@email.com', '$2b$12$CHCcxmQ5qNr4u6tWZwC1Nus.PK4fT7bYEnqiBZQnY8ZE1OmAQT6We', 'Maaz Javed', '7000000094', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000094', 'VOT3456789108', TRUE, TRUE, NOW()),
('madhur.chaudhari95@email.com', '$2b$12$nqVOC0McXyaxqnnr.MJwSe.LOdsbSG30yn232DYZofnVg5vv9QrLe', 'Madhur Chaudhari', '7000000095', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000095', 'VOT3456789109', TRUE, TRUE, NOW()),
('manohar.dudhat96@email.com', '$2b$12$il0fSHaHEQLR6hlOuB6Z8.8Q1Dy5fP1Umrvb.f9GzWvuttjAmf6ui', 'Manohar Dudhat', '7000000096', '2000-01-01', 'MALE', 'Ahmedabad City', 'Ahmedabad', 'Gujarat', '000000', '770000000096', 'VOT3456789110', TRUE, TRUE, NOW()),
('manoj.mn97@email.com', '$2b$12$Ood20eNiF/HQDq.eERbCt.Ad/ucSfvg1XKCW94Yhfceg/fdiy.zRC', 'Manoj MN', '7000000097', '2000-01-01', 'MALE', 'Bengaluru City', 'Bengaluru', 'Karnataka', '000000', '770000000097', 'VOT3456789111', TRUE, TRUE, NOW()),
('mayank.bansal98@email.com', '$2b$12$BYLFP1bbvBpfMmDBaSwgiuaFXmXlKyMSja86SV0GepcNKcqNxI0SS', 'Mayank Bansal', '7000000098', '2000-01-01', 'MALE', 'Delhi City', 'Delhi', 'Delhi', '000000', '770000000098', 'VOT3456789112', TRUE, TRUE, NOW()),
('mayank.chaudhary99@email.com', '$2b$12$p/BnAGbGREok3Q9m21kg6ujrBD7cOaKUuwD/PTeRDaSLKDEB2L3la', 'Mayank Chaudhary', '7000000099', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000099', 'VOT3456789113', TRUE, TRUE, NOW()),
('mayuri.narale100@email.com', '$2b$12$s7JAlm/46UvjgjEWeNKn8uUxD70Js705aRgld.v2sPa2NfS9ekO3q', 'Mayuri Narale', '7000000100', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000100', 'VOT3456789114', TRUE, TRUE, NOW()),
('md.zaman101@email.com', '$2b$12$pifTW4lDTG7Cpa8f/R8QS.ap8a16CuSuNhNAnuMDKUCYMQVTI4mQK', 'Md Zaman', '7000000101', '2000-01-01', 'MALE', 'Kolkata City', 'Kolkata', 'West Bengal', '000000', '770000000101', 'VOT3456789115', TRUE, TRUE, NOW()),
('mohammad.durrani102@email.com', '$2b$12$pMfcVFgiQlctxAAr7MKEg.CeYM0A6E1ZtawrlNWiQA3tthXeF5im.', 'Mohammad Durrani', '7000000102', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000102', 'VOT3456789116', TRUE, TRUE, NOW()),
('mourya.konada103@email.com', '$2b$12$0Za1pSsIl24eG2k2G9/MkOua3Nt/EJlBF7ccGzavAVcZfvlQNzKwi', 'Mourya Konada', '7000000103', '2000-01-01', 'MALE', 'Hyderabad City', 'Hyderabad', 'Telangana', '000000', '770000000103', 'VOT3456789117', TRUE, TRUE, NOW()),
('mousumi.das104@email.com', '$2b$12$vXJvLNoEtYsEoFbLdcwlueLipvAFNwEBcyTIgvpH9tKCfsUWttL0y', 'Mousumi Das', '7000000104', '2000-01-01', 'MALE', 'Kolkata City', 'Kolkata', 'West Bengal', '000000', '770000000104', 'VOT3456789118', TRUE, TRUE, NOW()),
('naina.yadav105@email.com', '$2b$12$/ztEZv3BvPpHMg40sihxiOpXEOhLFP0/y.Iz7jVHg745podStsA2m', 'Naina Yadav', '7000000105', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000105', 'VOT3456789119', TRUE, TRUE, NOW()),
('nentabaje.sakshi106@email.com', '$2b$12$BHHJLdS/0de.GuuBaON3M.r3Y88MvVjfHz1cM13aROFi9NAs4r4Ay', 'Nentabaje Sakshi', '7000000106', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000106', 'VOT3456789120', TRUE, TRUE, NOW()),
('nikita.deshmukh107@email.com', '$2b$12$aPzvjjjDLYR8JDiCSrh69OIYIuTDxEe.jFfr5aviXQ0KiBn/1ZPbu', 'Nikita Deshmukh', '7000000107', '2000-01-01', 'MALE', 'Nagpur City', 'Nagpur', 'Maharashtra', '000000', '770000000107', 'VOT3456789121', TRUE, TRUE, NOW()),
('nikita.patel108@email.com', '$2b$12$YdSYBCfRWcQ1OM4LICRK4.Gt1G6TRxUucnP6B4VR7OfdphD93BL4a', 'Nikita Patel', '7000000108', '2000-01-01', 'MALE', 'Ahmedabad City', 'Ahmedabad', 'Gujarat', '000000', '770000000108', 'VOT3456789122', TRUE, TRUE, NOW()),
('nimisha.verma109@email.com', '$2b$12$vauqBX0YOwSMniI1HBPN2.uuytZtAOUz0N4fn4leNvllWeNq40LBi', 'Nimisha Verma', '7000000109', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000109', 'VOT3456789123', TRUE, TRUE, NOW()),
('niraj.gehlot110@email.com', '$2b$12$bjfqF.6qSdtCG.N2UE6BAe9U4SPKrEmHnMrwJhmcD8smCK42y0PRC', 'Niraj Gehlot', '7000000110', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000110', 'VOT3456789124', TRUE, TRUE, NOW()),
('nirupama.rajana111@email.com', '$2b$12$IMey9gPi3r3ogD0zK2qG3uccQRd.CIY3iq8qvzlpfyqLKZYRHm2RO', 'Nirupama Rajana', '7000000111', '2000-01-01', 'MALE', 'Hyderabad City', 'Hyderabad', 'Telangana', '000000', '770000000111', 'VOT3456789125', TRUE, TRUE, NOW()),
('nishant.vij112@email.com', '$2b$12$eLjSU7gLAbETcpFAZXMZv.lWQVu3MFd19aFmUfQhvGhStvIJQxOwm', 'Nishant Vij', '7000000112', '2000-01-01', 'MALE', 'Delhi City', 'Delhi', 'Delhi', '000000', '770000000112', 'VOT3456789126', TRUE, TRUE, NOW()),
('nishi.ranjan113@email.com', '$2b$12$7TZWuKrP3uf6b/EBPyUsLOSj/PUaIRhzVNm.4y88nPZd1QV8ICSYe', 'Nishi Ranjan', '7000000113', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000113', 'VOT3456789127', TRUE, TRUE, NOW()),
('onkar.jogdand114@email.com', '$2b$12$uaDesuskWoMscTm6vRWN7.PHdiA3XHseeN6NoSMtqMR1N7EOwL9PW', 'Onkar Jogdand', '7000000114', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000114', 'VOT3456789128', TRUE, TRUE, NOW()),
('palak.sirothiya115@email.com', '$2b$12$4mI0qka.tEVTE0YRv/aM5.ZUK8DJLTRIjkizTQJUZXn1Ki0rfFr4m', 'Palak Sirothiya', '7000000115', '2000-01-01', 'MALE', 'Indore City', 'Indore', 'Madhya Pradesh', '000000', '770000000115', 'VOT3456789129', TRUE, TRUE, NOW()),
('pittala.soumya116@email.com', '$2b$12$KVlY9VAyOX5K/rDk7wfn3.FxhhTbgEChb.A78Nvq/ud83aPtp8Tmq', 'Pittala Soumya', '7000000116', '2000-01-01', 'MALE', 'Vijayawada City', 'Vijayawada', 'Andhra Pradesh', '000000', '770000000116', 'VOT3456789130', TRUE, TRUE, NOW()),
('prabhat.kumar117@email.com', '$2b$12$tpRVXoacf8HY4AjcRueoseIQu6eHbo3gdPXX40D8cnnAS95TvF5lK', 'Prabhat Kumar', '7000000117', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000117', 'VOT3456789131', TRUE, TRUE, NOW()),
('prajakta.derle118@email.com', '$2b$12$R7V2eBfksvYByJzsrEd58eUx3Mau3tzW6WkgO8bAy6ceFBzDCZ6ti', 'Prajakta Derle', '7000000118', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000118', 'VOT3456789132', TRUE, TRUE, NOW()),
('prajwal.patil119@email.com', '$2b$12$yhW0duXcLR8wotkdSjX/oOAQAddhbj1dfefFecrA6vpiBq1G0HLCS', 'Prajwal Patil', '7000000119', '2000-01-01', 'MALE', 'Mumbai City', 'Mumbai', 'Maharashtra', '000000', '770000000119', 'VOT3456789133', TRUE, TRUE, NOW()),
('prakhar.singh120@email.com', '$2b$12$hvBtjt9kbPa7aktGXm9Ja.QwJtizOBNsrXE5sKv.6HAqgCUiw6s0.', 'Prakhar Singh', '7000000120', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000120', 'VOT3456789134', TRUE, TRUE, NOW()),
('pranav.nair121@email.com', '$2b$12$fVhAqoYuqoMDhtB8yO/Ug.3MNg2XTgrjjfhuIDJgSYQ/RX7/zUUZa', 'Pranav Nair', '7000000121', '2000-01-01', 'MALE', 'Kochi City', 'Kochi', 'Kerala', '000000', '770000000121', 'VOT3456789135', TRUE, TRUE, NOW()),
('pranavi122@email.com', '$2b$12$U7eB2FQ9PFoldkd3ZXgerOiH7yzzj5/BXQINSjcuL7CL9ABgjer3K', 'Pranavi', '7000000122', '2000-01-01', 'MALE', 'Hyderabad City', 'Hyderabad', 'Telangana', '000000', '770000000122', 'VOT3456789137', TRUE, TRUE, NOW()),
('pranjal.agrawal123@email.com', '$2b$12$N1BcngkiJkE98SxueHdGee36Rvd4cXZW7JMh6TjqvG6plM.3HSv6S', 'Pranjal Agrawal', '7000000123', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000123', 'VOT3456789138', TRUE, TRUE, NOW()),
('pratiksha.lande124@email.com', '$2b$12$0KnQw/jxyM1kUGJg40CYPurFetcCimYZ5V83duNqV2i59lmwq1yya', 'Pratiksha Lande', '7000000124', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000124', 'VOT3456789139', TRUE, TRUE, NOW()),
('priyanshu.patidar125@email.com', '$2b$12$Lhn/NzcGx.prsv4iOs7rSegQubFp3TBFNr6EYUQEAvXvpqie6eQ2u', 'Priyanshu Patidar', '7000000125', '2000-01-01', 'MALE', 'Indore City', 'Indore', 'Madhya Pradesh', '000000', '770000000125', 'VOT3456789140', TRUE, TRUE, NOW()),
('priyanshu.raj126@email.com', '$2b$12$fhNsOq7BKMrLxl5ROs7CAO23uI083OzQ6JuoDpiyaD5LngDl2Fjcy', 'Priyanshu Raj', '7000000126', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000126', 'VOT3456789141', TRUE, TRUE, NOW()),
('pruthvi.bhat127@email.com', '$2b$12$2Yiu/AF60bTlPYVKZEGbeuDNut9zyEuUy9yuAK9wm1gVUiIgVbaZC', 'Pruthvi Bhat', '7000000127', '2000-01-01', 'MALE', 'Mangalore City', 'Mangalore', 'Karnataka', '000000', '770000000127', 'VOT3456789142', TRUE, TRUE, NOW()),
('raghavendra.uppar128@email.com', '$2b$12$88CVz96zt7oKYLiOXUB2I.BNaO6MXNrQo4Vrdej3LeQ.epJfTPB.W', 'Raghavendra Uppar', '7000000128', '2000-01-01', 'MALE', 'Bengaluru City', 'Bengaluru', 'Karnataka', '000000', '770000000128', 'VOT3456789143', TRUE, TRUE, NOW()),
('raj.payasi129@email.com', '$2b$12$A2rmMwIAMEOS.7/5effjA.ekWZNXQkyaxE65HMDG.bFzZMriTa1sW', 'Raj Payasi', '7000000129', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000129', 'VOT3456789144', TRUE, TRUE, NOW()),
('rajat.mourya130@email.com', '$2b$12$UFb6sVY1En1c9qkF2Hpbp.VietXAKWlWHetqydCsCxS2hZJIwfX8G', 'Rajat Mourya', '7000000130', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000130', 'VOT3456789145', TRUE, TRUE, NOW()),
('rajdeep.kala131@email.com', '$2b$12$0dFbDnwO8Nnpw8kXQWK0keokUuBFsLWD2P4wNHTbtSi1F6n/teeR2', 'Rajdeep Kala', '7000000131', '2000-01-01', 'MALE', 'Amritsar City', 'Amritsar', 'Punjab', '000000', '770000000131', 'VOT3456789146', TRUE, TRUE, NOW()),
('rajnish.singh132@email.com', '$2b$12$.vwtk71Su9IB/fO72dVkOOvCxcmBZgV5cL2S8bEvGWKxFhsridkqO', 'Rajnish Singh', '7000000132', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000132', 'VOT3456789147', TRUE, TRUE, NOW()),
('ram.wandile133@email.com', '$2b$12$gb/3UAVxOpj5T553JNcKnuhnWQbShDEI/Nc0NvWAM9MpGa/rdG4Hq', 'Ram Wandile', '7000000133', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000133', 'VOT3456789148', TRUE, TRUE, NOW()),
('ratnesh.raja134@email.com', '$2b$12$Wb/91SFrIALS1UL4kBvWFe85qHp0bxX0Mrr/3Xhv/JV9fNtdcKnd.', 'Ratnesh Raja', '7000000134', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000134', 'VOT3456789149', TRUE, TRUE, NOW()),
('rishi.singh135@email.com', '$2b$12$ypL/uwC/7ERmVVYL7haX5OYDMLElHVcPWgAEq.zC9zrBF0XdDbff6', 'Rishi Singh', '7000000135', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000135', 'VOT3456789151', TRUE, TRUE, NOW()),
('ritesh.singh136@email.com', '$2b$12$BUquEvkEVx.MS5/mgOBfG.BjGq2oLgzEGhY0GPS54Vu19laDxDEx6', 'Ritesh Singh', '7000000136', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000136', 'VOT3456789152', TRUE, TRUE, NOW()),
('ritik.bhattal137@email.com', '$2b$12$LR0a.y/ARw9xZTCB2NnZheiTbx9FzJnBoBKq9SHaUqV6/X.b7m7FC', 'Ritik Bhattal', '7000000137', '2000-01-01', 'MALE', 'Chandigarh City', 'Chandigarh', 'Punjab', '000000', '770000000137', 'VOT3456789153', TRUE, TRUE, NOW()),
('ronit.singh138@email.com', '$2b$12$k4R9SlmIP3Ys6WrWouqjb.LQor68PBC7.ZhbpbqBTd1WLBkIsnHym', 'Ronit Singh', '7000000138', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000138', 'VOT3456789154', TRUE, TRUE, NOW()),
('rupesh.patil139@email.com', '$2b$12$0BYckzT/f9e7OaY/2qcyPeIdeRtx7k7e1grMr/38qehmBPx92z1NO', 'Rupesh Patil', '7000000139', '2000-01-01', 'MALE', 'Mumbai City', 'Mumbai', 'Maharashtra', '000000', '770000000139', 'VOT3456789155', TRUE, TRUE, NOW()),
('rutuja.khot140@email.com', '$2b$12$rvQPwrKYIFQiYC5.SdNMOO.OaNbSSNDRaStIs1lse.K2DuRcutRRO', 'Rutuja Khot', '7000000140', '2000-01-01', 'MALE', 'Mumbai City', 'Mumbai', 'Maharashtra', '000000', '770000000140', 'VOT3456789157', TRUE, TRUE, NOW()),
('s.pranav141@email.com', '$2b$12$CMW6eMJ1XAW0kxZF/7kwi.ZKfOm8xeSWZdVzrbAbrayKV6iG3WLtq', 'S Pranav', '7000000141', '2000-01-01', 'MALE', 'Chennai City', 'Chennai', 'Tamil Nadu', '000000', '770000000141', 'VOT3456789158', TRUE, TRUE, NOW()),
('sahil.kadam142@email.com', '$2b$12$ordjEmdfRCa4yJCdsmQRBeVlR0LlisD359h9KWMr3.sf3HhpaQrSi', 'Sahil Kadam', '7000000142', '2000-01-01', 'MALE', 'Mumbai City', 'Mumbai', 'Maharashtra', '000000', '770000000142', 'VOT3456789159', TRUE, TRUE, NOW()),
('saket.bagre143@email.com', '$2b$12$Bui0034gEbZOpLMICbAkCO8DN04eDCQUEbHh/9cEfHrc723pLrgni', 'Saket Bagre', '7000000143', '2000-01-01', 'MALE', 'Indore City', 'Indore', 'Madhya Pradesh', '000000', '770000000143', 'VOT3456789160', TRUE, TRUE, NOW()),
('saksham.jain144@email.com', '$2b$12$Cjr0VgmfpvjXCWegv2sfIOQWiqVSXv23hAMEAQ2or2srpICAUlaiK', 'Saksham Jain', '7000000144', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000144', 'VOT3456789161', TRUE, TRUE, NOW()),
('sakshi.pimpale145@email.com', '$2b$12$BYyrlH7fq7hEwKYsXeGTfuvypZNfWceq25lzkfqBKrFNuLoFmV3TC', 'Sakshi Pimpale', '7000000145', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000145', 'VOT3456789162', TRUE, TRUE, NOW()),
('sanskar.chaurasia146@email.com', '$2b$12$PzYqbhQ2p.1Ag.cVT8QrieTSfqTgfAAyYqQOFx97nNkEW6iDN/Z5C', 'Sanskar Chaurasia', '7000000146', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000146', 'VOT3456789163', TRUE, TRUE, NOW()),
('sarode.mamata147@email.com', '$2b$12$qmdAhjV3foIZ7IKFiuPSH.cohoP5dTaemIFpc0mhIxEB7MzSZpVwe', 'Sarode Mamata', '7000000147', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000147', 'VOT3456789164', TRUE, TRUE, NOW()),
('sarthak.gore148@email.com', '$2b$12$swbV05S6XhYzJNlBLaNqh.rGjLAfFTS5sh7BEfNiKq0Nl6qxz/kFG', 'Sarthak Gore', '7000000148', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000148', 'VOT3456789165', TRUE, TRUE, NOW()),
('satyam.patel149@email.com', '$2b$12$rQuhF62HRdZFG/b4NupimOaNEtw26kM56psLD7P62T69m103rEZEm', 'Satyam Patel', '7000000149', '2000-01-01', 'MALE', 'Surat City', 'Surat', 'Gujarat', '000000', '770000000149', 'VOT3456789166', TRUE, TRUE, NOW()),
('satyendra.rathore150@email.com', '$2b$12$mdpdC8o6UEO/8j6oPqg2M..EoNvYAo86/OWgMTJw.1xzy./6CD9lK', 'Satyendra Rathore', '7000000150', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000150', 'VOT3456789167', TRUE, TRUE, NOW()),
('saurabh.mali151@email.com', '$2b$12$3vaml7I2S8316lu80ES9VeaO9Ob5LQpibIfDR5fI8.vu6pJp2wM3e', 'Saurabh Mali', '7000000151', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000151', 'VOT3456789168', TRUE, TRUE, NOW()),
('sayma.ahmad152@email.com', '$2b$12$oyiitTP7cUowoDfSCgUBlO7prsYVy1TDJnJR1acyp8RNbmBJu3lA6', 'Sayma Ahmad', '7000000152', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000152', 'VOT3456789169', TRUE, TRUE, NOW()),
('shivansh.mishra153@email.com', '$2b$12$v4jxFFKRvMz3PrZpJII9gO7XMo9me5cxjUGr5HxWVRAOIOTpfpoAi', 'Shivansh Mishra', '7000000153', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000153', 'VOT3456789170', TRUE, TRUE, NOW()),
('shreyansh.shah154@email.com', '$2b$12$ftRFRN6LJIc/N/2iqOZGx.VfVDuaYDkoQXeaCRO8.b1RDn/7D2QTO', 'Shreyansh Shah', '7000000154', '2000-01-01', 'MALE', 'Ahmedabad City', 'Ahmedabad', 'Gujarat', '000000', '770000000154', 'VOT3456789171', TRUE, TRUE, NOW()),
('shristi.mishra155@email.com', '$2b$12$XlPk1SWS2sSx6wqY79UMYOSI/qJv0PfA1DMVFCgohksRl84rvJutG', 'Shristi Mishra', '7000000155', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000155', 'VOT3456789172', TRUE, TRUE, NOW()),
('shubham.dhakate156@email.com', '$2b$12$9qmd/qN36862.mV4ERSDRu4tPpaBt2I2cpaUjgIcEE849glq3IEee', 'Shubham Dhakate', '7000000156', '2000-01-01', 'MALE', 'Nagpur City', 'Nagpur', 'Maharashtra', '000000', '770000000156', 'VOT3456789173', TRUE, TRUE, NOW()),
('shubham.kumar157@email.com', '$2b$12$dw37s3E23/6yefA..wyBD.46vY6S73jp9DtDbH57nxvaxCPicihne', 'Shubham Kumar', '7000000157', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000157', 'VOT3456789174', TRUE, TRUE, NOW()),
('shubham.rahangdale158@email.com', '$2b$12$7SDNoftM4IReA8TAoCt4Xew6vlFKlfal96y2KtGZP/fwZrauFqtVi', 'Shubham Rahangdale', '7000000158', '2000-01-01', 'MALE', 'Nagpur City', 'Nagpur', 'Maharashtra', '000000', '770000000158', 'VOT3456789175', TRUE, TRUE, NOW()),
('shubhra.tiwari159@email.com', '$2b$12$6uluQ1ZP6/D9h3dbnGQT2.OxkfbSW7uzN0o1dfTo76xclePZ.r.yu', 'Shubhra Tiwari', '7000000159', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000159', 'VOT3456789176', TRUE, TRUE, NOW()),
('sonit.nagpure160@email.com', '$2b$12$L7Mfa4TU91iPaBK0ozubJ.2v8Zi5lBuSf8ki/IrtjciL53wQs5.TO', 'Sonit Nagpure', '7000000160', '2000-01-01', 'MALE', 'Nagpur City', 'Nagpur', 'Maharashtra', '000000', '770000000160', 'VOT3456789178', TRUE, TRUE, NOW()),
('soumya.bharti161@email.com', '$2b$12$Kjn19DijDsEc4BP0vmogQe9XL6PF3/mwwK9Bn5nqc4ITdwdbxMoJG', 'Soumya Bharti', '7000000161', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000161', 'VOT3456789179', TRUE, TRUE, NOW()),
('soumya.singh162@email.com', '$2b$12$zxE1KgMJM7nfAr16rzIRousTHlWhLAyWTWWekDxQaL0Z5Kgufv8XK', 'Soumya Singh', '7000000162', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000162', 'VOT3456789180', TRUE, TRUE, NOW()),
('sriram.s163@email.com', '$2b$12$6emQR3olVkg334QVdAxg9evX/1kxxN.oiy.E1wXUsNSPyjwAEMGkq', 'Sriram S', '7000000163', '2000-01-01', 'MALE', 'Chennai City', 'Chennai', 'Tamil Nadu', '000000', '770000000163', 'VOT3456789181', TRUE, TRUE, NOW()),
('stalin.kanjiramparambil164@email.com', '$2b$12$9IGteHeJygaEqZRR4QrDQ.4gpbSRTXmps5q4bCTOOTUo6Eulj4RES', 'Stalin Kanjiramparambil', '7000000164', '2000-01-01', 'MALE', 'Kochi City', 'Kochi', 'Kerala', '000000', '770000000164', 'VOT3456789182', TRUE, TRUE, NOW()),
('sumit.kumar165@email.com', '$2b$12$UhBZFvGcX.qi8XzxOVxmbeilEcQZYJcaURqtvZRDYcWxCHKTYBFbu', 'Sumit Kumar', '7000000165', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000165', 'VOT3456789183', TRUE, TRUE, NOW()),
('sumit.sharma166@email.com', '$2b$12$8dBqc8tPpdAoi7qmmy9MyuKVvuKHSmkwaPWssr07G.ggOaZLYOhXG', 'Sumit Sharma', '7000000166', '2000-01-01', 'MALE', 'Jaipur City', 'Jaipur', 'Rajasthan', '000000', '770000000166', 'VOT3456789184', TRUE, TRUE, NOW()),
('suyesh.dubey167@email.com', '$2b$12$41SL4O7ZKxW/IZh6296V2OENBQwNvTbqQ7mOrF1qI6zkZhgcYnrh2', 'Suyesh Dubey', '7000000167', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000167', 'VOT3456789185', TRUE, TRUE, NOW()),
('suyog.tathe168@email.com', '$2b$12$pAoik.fNGjS3l/7lJQOBNObjP6qx7Ip9riYHWEK2BAEGbC1DQd5XW', 'Suyog Tathe', '7000000168', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000168', 'VOT3456789186', TRUE, TRUE, NOW()),
('swarnil.kokulwar169@email.com', '$2b$12$DkIA6ElvF3f441A5bPh2B.2/VSQOvFkrbXO48WX3Wvri28V9CcI9u', 'Swarnil Kokulwar', '7000000169', '2000-01-01', 'MALE', 'Nagpur City', 'Nagpur', 'Maharashtra', '000000', '770000000169', 'VOT3456789187', TRUE, TRUE, NOW()),
('swathi.simbothula170@email.com', '$2b$12$ESadoSzEc.ykc4Fs.RI/1ukT1YuG/XJSoQefxzwWU7TnxuPTz25cy', 'Swathi Simbothula', '7000000170', '2000-01-01', 'MALE', 'Hyderabad City', 'Hyderabad', 'Telangana', '000000', '770000000170', 'VOT3456789188', TRUE, TRUE, NOW()),
('swati.kumari171@email.com', '$2b$12$ipWFo1UiDE0EboHrTaUbR.01Sr16uz9K0WpFyXoV/mParNDF88N0W', 'Swati Kumari', '7000000171', '2000-01-01', 'MALE', 'Patna City', 'Patna', 'Bihar', '000000', '770000000171', 'VOT3456789189', TRUE, TRUE, NOW()),
('sweekar.chougale172@email.com', '$2b$12$sYpqlquB.pVPfXy2G8VfteGzF2BN6p57QvJ2EMR10Oa10iFJOlaUq', 'Sweekar Chougale', '7000000172', '2000-01-01', 'MALE', 'Belgaum City', 'Belgaum', 'Karnataka', '000000', '770000000172', 'VOT3456789190', TRUE, TRUE, NOW()),
('tanishq.jarsodiwala173@email.com', '$2b$12$o9WWigiG0M3rivFmCNRDB.tMzBbmfZsNtwgR/5DDM45//2aUbNcpy', 'Tanishq Jarsodiwala', '7000000173', '2000-01-01', 'MALE', 'Mumbai City', 'Mumbai', 'Maharashtra', '000000', '770000000173', 'VOT3456789191', TRUE, TRUE, NOW()),
('tanuja.ghaytidak174@email.com', '$2b$12$Cv9Ia3RTXB4LIbFhwElBzeljevLa2zvf.LUf.Av8iIXq8iA2mwh6e', 'Tanuja Ghaytidak', '7000000174', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000174', 'VOT3456789192', TRUE, TRUE, NOW()),
('tanvi.gundarwar175@email.com', '$2b$12$fnzBPejRpPMTI4qiAFovXOnZ6BnaLK8gTcp6eTkJ2b6meGxFhieO.', 'Tanvi Gundarwar', '7000000175', '2000-01-01', 'MALE', 'Nagpur City', 'Nagpur', 'Maharashtra', '000000', '770000000175', 'VOT3456789193', TRUE, TRUE, NOW()),
('tanya.naidu176@email.com', '$2b$12$f/qA7.3a7vn3of5rYHL13OmnA2qfeNFKDfntvBBn4NEHYwQC5sC0K', 'Tanya Naidu', '7000000176', '2000-01-01', 'MALE', 'Hyderabad City', 'Hyderabad', 'Telangana', '000000', '770000000176', 'VOT3456789194', TRUE, TRUE, NOW()),
('titiksha.chandrakar177@email.com', '$2b$12$HgqcUeaJYHY2B3dVDb0f3eM6Txapp6uXPPIXssapPXRkhs.IC5zHW', 'Titiksha Chandrakar', '7000000177', '2000-01-01', 'MALE', 'Raipur City', 'Raipur', 'Chhattisgarh', '000000', '770000000177', 'VOT3456789195', TRUE, TRUE, NOW()),
('tushar.patil178@email.com', '$2b$12$nZG33/kSVg31Dr3I261qouAIUXYUM7z8d18H8ipXmyaW59KJlT2he', 'Tushar Patil', '7000000178', '2000-01-01', 'MALE', 'Mumbai City', 'Mumbai', 'Maharashtra', '000000', '770000000178', 'VOT3456789196', TRUE, TRUE, NOW()),
('ujjwal.chourasia179@email.com', '$2b$12$q.hgsXbhiWQKuc6JpnjXCu.btCB9h0B16DoyrdfXoHi9syWFTVTKK', 'Ujjwal Chourasia', '7000000179', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000179', 'VOT3456789197', TRUE, TRUE, NOW()),
('vaishnav.uke180@email.com', '$2b$12$Fj7vXHHpurbxClJw66P47u4lBISQilkJoqaOxvJItgqw1WAMI3owC', 'Vaishnav Uke', '7000000180', '2000-01-01', 'MALE', 'Pune City', 'Pune', 'Maharashtra', '000000', '770000000180', 'VOT3456789199', TRUE, TRUE, NOW()),
('vansh.dhiman181@email.com', '$2b$12$EmsAoqZiWSPikZne9zrYveKyHAERvhivDWlQuxfailoNq04LrM7aa', 'Vansh Dhiman', '7000000181', '2000-01-01', 'MALE', 'Chandigarh City', 'Chandigarh', 'Punjab', '000000', '770000000181', 'VOT3456789200', TRUE, TRUE, NOW()),
('vibhor.jayaswal182@email.com', '$2b$12$TpmIrAU2fn8tmARlXtkEv.ou9sxJNcUoBkwd6unYXZLmCqMuvSRcO', 'Vibhor Jayaswal', '7000000182', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000182', 'VOT3456789201', TRUE, TRUE, NOW()),
('vikhyat.gupta183@email.com', '$2b$12$wEn0nUhJFHF/r306WNBdgevW.1ICFcJaFHpHCwhd.oc4akBbYqbpC', 'Vikhyat Gupta', '7000000183', '2000-01-01', 'MALE', 'Delhi City', 'Delhi', 'Delhi', '000000', '770000000183', 'VOT3456789202', TRUE, TRUE, NOW()),
('vishal184@email.com', '$2b$12$uXZ1neGEA88gSh3z6TdgzOvtCFRaMOjwp51RPkXc/zzBdxvAzxUXW', 'Vishal', '7000000184', '2000-01-01', 'MALE', 'Delhi City', 'Delhi', 'Delhi', '000000', '770000000184', 'VOT3456789203', TRUE, TRUE, NOW()),
('vishal.rawat185@email.com', '$2b$12$.faNENnclNLNSSxHSHHz8O2cXTo5nr8WNonGTW1/wRnxWGykd7VTi', 'Vishal Rawat', '7000000185', '2000-01-01', 'MALE', 'Dehradun City', 'Dehradun', 'Uttarakhand', '000000', '770000000185', 'VOT3456789204', TRUE, TRUE, NOW()),
('yash.dudhe186@email.com', '$2b$12$L96ixHEw06470pY9F/QgKe5AzMBXoOxGm35oUYhroMuOoGZ4UglEy', 'Yash Dudhe', '7000000186', '2000-01-01', 'MALE', 'Nagpur City', 'Nagpur', 'Maharashtra', '000000', '770000000186', 'VOT3456789206', TRUE, TRUE, NOW()),
('yash.mourya187@email.com', '$2b$12$.cp3i43R0/0mNDNrLT.HkuhZMnsItRvZGskX/alIgeyCU30voOMfi', 'Yash Mourya', '7000000187', '2000-01-01', 'MALE', 'Lucknow City', 'Lucknow', 'Uttar Pradesh', '000000', '770000000187', 'VOT3456789207', TRUE, TRUE, NOW()),
('yash.shirude188@email.com', '$2b$12$JCG2msB6BOJFaM5e4SFpn.3jgodzUKLlurq6Na2MX9YhA2l47.HHq', 'Yash Shirude', '7000000188', '2000-01-01', 'MALE', 'Mumbai City', 'Mumbai', 'Maharashtra', '000000', '770000000188', 'VOT3456789208', TRUE, TRUE, NOW()),
('yash.sonkuwar189@email.com', '$2b$12$bYCMpsub7nklC6O0VwXyhua6bNYbQHBUxJ2IWtn..g1zTzotX6ffO', 'Yash Sonkuwar', '7000000189', '2000-01-01', 'MALE', 'Nagpur City', 'Nagpur', 'Maharashtra', '000000', '770000000189', 'VOT3456789209', TRUE, TRUE, NOW());

-- ============================================
-- NEW REGIONAL ELECTIONS (UP, MP, INDORE)
-- ============================================

-- 1. INSERT NEW ACTIVE ELECTIONS
INSERT INTO elections (election_name, election_type, start_date, end_date, city, state, status, result_published, created_by) VALUES
('Uttar Pradesh State Assembly Election 2026', 'STATE', '2026-02-01 08:00:00', '2026-02-10 18:00:00', NULL, 'Uttar Pradesh', 'ACTIVE', FALSE, 1),
('Madhya Pradesh State Assembly Election 2026', 'STATE', '2026-02-05 08:00:00', '2026-02-15 18:00:00', NULL, 'Madhya Pradesh', 'ACTIVE', FALSE, 1),
('Indore Municipal Corporation Election 2026', 'LOCAL', '2026-02-05 08:00:00', '2026-02-08 18:00:00', 'Indore', 'Madhya Pradesh', 'ACTIVE', FALSE, 2);

-- Note: IDs are likely 5, 6, 7 respectively assuming previous were 1-4.
-- Storing IDs in variables is not supported in standard SQL scripts usually, so we assume 5, 6, 7.

-- 2. INSERT CANDIDATES
-- For UP (Election ID 5)
INSERT INTO candidates (user_id, election_id, party_name, party_symbol, manifesto) VALUES
(21, 5, 'Bharatiya Janata Party', 'Lotus', 'Development for UP'), -- Madhur Chaudhari (Actually from Jalgaon/Pune in list but using for demo or picking distinct users)
-- Wait, let's pick users who are NOT likely to vote in these elections or just valid generic users. 
-- The Candidates table references user_id. Candidate users don't have to be from that region necessarily (though usually are).
-- Let's use users 21, 22, 23 for UP candidates (assuming they are candidates).
(35, 5, 'Samajwadi Party', 'Bicycle', 'Social Justice'),
(22, 5, 'Bahujan Samaj Party', 'Elephant', 'Inclusive Growth'),
(23, 5, 'Indian National Congress', 'Hand', 'Change for Progress');

-- For MP (Election ID 6)
INSERT INTO candidates (user_id, election_id, party_name, party_symbol, manifesto) VALUES
(24, 6, 'Bharatiya Janata Party', 'Lotus', 'MP ke Man me Modi'),
(25, 6, 'Indian National Congress', 'Hand', 'Welfare for MP'),
(26, 6, 'Aam Aadmi Party', 'Broom', 'Clean Politics');

-- For Indore (Election ID 7)
INSERT INTO candidates (user_id, election_id, party_name, party_symbol, manifesto) VALUES
(27, 7, 'Bharatiya Janata Party', 'Lotus', 'Cleanest City Forever'),
(28, 7, 'Indian National Congress', 'Hand', 'Better Infrastructure'),
(29, 7, 'Independent', 'Bat', 'Local Voice');

-- 3. INSERT VOTES (Simulate some voting)
-- UP Voters (State = Uttar Pradesh)
-- Users: Anuj Tomar (Agra), Rajat Morya (Bareilly), Ayush Kush (Lucknow), etc.
-- Let's assume User IDs for them.
-- From the script: 
-- Anuj Tomar -> ID 22 (created in '7. NEW USERS...' block)
-- Rajat Morya -> ID 26
-- Ayush Kush -> ID 27
-- Prabal Singh -> ID 36
-- Anuj Sharma -> ID 41
-- Apoorv Singh -> ID 49
-- Pranav Tiwari -> ID 50
-- Plus the bulk users starting from ID 51...
-- Let's use subqueries to be safe or just assume IDs based on the insert order in finalschema.sql

-- Voting in UP Election (ID 5)
-- Anuj Tomar (22) votes for Candidate 1 (User 21 - ID in candidates table is auto-inc, let's say 21)
-- Candidate IDs:
-- UP: 21, 22, 23 -> Candidate IDs 21, 22, 23
-- MP: 24, 25, 26 -> Candidate IDs 24, 25, 26
-- Indore: 27, 28, 29 -> Candidate IDs 27, 28, 29
-- (Assuming continuous auto-inc from 20)

INSERT INTO votes (election_id, user_id, candidate_id, vote_hash, voted_at) VALUES
(5, 22, 21, CONCAT('hash_e5_u22_', MD5(NOW())), NOW()), -- Anuj Tomar votes
(5, 26, 22, CONCAT('hash_e5_u26_', MD5(NOW())), NOW()), -- Rajat Morya votes
(5, 27, 21, CONCAT('hash_e5_u27_', MD5(NOW())), NOW()); -- Ayush Kush votes

INSERT INTO voter_election_status (election_id, user_id, has_voted, voted_at) VALUES
(5, 22, TRUE, NOW()),
(5, 26, TRUE, NOW()),
(5, 27, TRUE, NOW()),
(5, 36, FALSE, NULL), -- Prabal Singh (Not voted)
(5, 41, FALSE, NULL); -- Anuj Sharma (Not voted)

-- Voting in MP Election (ID 6)
-- Users: Satyam Patel (Katni - 23), Satyam Bavankar (Seoni - 30), Ansh Mittal (Gwalior - 33), Rajdeep Kala (Ujjain - 34)
INSERT INTO votes (election_id, user_id, candidate_id, vote_hash, voted_at) VALUES
(6, 23, 24, CONCAT('hash_e6_u23_', MD5(NOW())), NOW()), -- Satyam Patel votes
(6, 30, 25, CONCAT('hash_e6_u30_', MD5(NOW())), NOW()); -- Satyam Bavankar votes

INSERT INTO voter_election_status (election_id, user_id, has_voted, voted_at) VALUES
(6, 23, TRUE, NOW()),
(6, 30, TRUE, NOW()),
(6, 33, FALSE, NULL), -- Ansh Mittal
(6, 34, FALSE, NULL); -- Rajdeep Kala

-- Voting in Indore Election (ID 7) - LOCAL
-- Users: Pooja Joshi (Indore - 12), Himanshu Muleva (40), Abhijeet Singh (51)
-- Also generic users: Abhijit Mandloi (Indore), Ansh Jain, etc.
INSERT INTO votes (election_id, user_id, candidate_id, vote_hash, voted_at) VALUES
(7, 12, 27, CONCAT('hash_e7_u12_', MD5(NOW())), NOW()), -- Pooja Joshi votes
(7, 40, 27, CONCAT('hash_e7_u40_', MD5(NOW())), NOW()); -- Himanshu Muleva votes

INSERT INTO voter_election_status (election_id, user_id, has_voted, voted_at) VALUES
(7, 12, TRUE, NOW()),
(7, 40, TRUE, NOW()),
(7, 51, FALSE, NULL); -- Abhijeet Singh



```