CREATE DATABASE devovs;



USE devovs;



-- 1. Create users table first (no dependencies)

CREATE TABLE users (

&nbsp;   user\_id BIGINT PRIMARY KEY AUTO\_INCREMENT,

&nbsp;   email VARCHAR(255) UNIQUE NOT NULL,

&nbsp;   password\_hash VARCHAR(255) NOT NULL,

&nbsp;   full\_name VARCHAR(255) NOT NULL,

&nbsp;   phone\_number VARCHAR(20),

&nbsp;   date\_of\_birth DATE NOT NULL,

&nbsp;   gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,

&nbsp;   address TEXT,

&nbsp;   city VARCHAR(100),

&nbsp;   state VARCHAR(100),

&nbsp;   pincode VARCHAR(10),

&nbsp;   id\_proof\_type ENUM('AADHAR', 'PAN', 'VOTER\_ID', 'PASSPORT') NOT NULL,

&nbsp;   profile\_image\_url VARCHAR(500),

&nbsp;   role ENUM('VOTER', 'ADMIN') NOT NULL DEFAULT 'VOTER',

&nbsp;   is\_active BOOLEAN DEFAULT TRUE,

&nbsp;   is\_verified BOOLEAN DEFAULT FALSE,

&nbsp;   registration\_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',

&nbsp;   approved\_at TIMESTAMP NULL,

&nbsp;   created\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,

&nbsp;   updated\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP,

&nbsp;   INDEX idx\_email (email),

&nbsp;   INDEX idx\_role (role),

&nbsp;   INDEX idx\_registration\_status (registration\_status)

);



-- 2. Create admins table (depends on users)

CREATE TABLE admins (

&nbsp;   admin\_id BIGINT PRIMARY KEY AUTO\_INCREMENT,

&nbsp;   user\_id BIGINT UNIQUE NOT NULL,

&nbsp;   assigned\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,

&nbsp;   FOREIGN KEY (user\_id) REFERENCES users(user\_id) ON DELETE CASCADE

);



-- 3. Create elections table BEFORE candidates (depends on users)

CREATE TABLE elections (

&nbsp;   election\_id BIGINT PRIMARY KEY AUTO\_INCREMENT,

&nbsp;   election\_name VARCHAR(255) NOT NULL,

&nbsp;   election\_type ENUM('GENERAL', 'STATE', 'LOCAL', 'SPECIAL') NOT NULL,

&nbsp;   start\_date DATETIME NOT NULL,

&nbsp;   end\_date DATETIME NOT NULL,

&nbsp;   status ENUM('DRAFT', 'SCHEDULED', 'ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'DRAFT',

&nbsp;   result\_published BOOLEAN DEFAULT FALSE,

&nbsp;   result\_published\_at TIMESTAMP NULL,

&nbsp;   result\_published\_by BIGINT NULL,

&nbsp;   created\_by BIGINT NOT NULL,

&nbsp;   created\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,

&nbsp;   FOREIGN KEY (created\_by) REFERENCES users(user\_id) ON DELETE RESTRICT,

&nbsp;   FOREIGN KEY (result\_published\_by) REFERENCES users(user\_id) ON DELETE SET NULL,

&nbsp;   INDEX idx\_election\_dates (start\_date, end\_date),

&nbsp;   INDEX idx\_status (status)

);



-- 4. NOW create candidates table (MODIFIED - removed fields)

CREATE TABLE candidates (

&nbsp;   candidate\_id BIGINT PRIMARY KEY AUTO\_INCREMENT,

&nbsp;   user\_id BIGINT NULL,

&nbsp;   election\_id BIGINT NOT NULL,

&nbsp;   party\_name VARCHAR(255),

&nbsp;   candidate\_symbol VARCHAR(255),

&nbsp;   candidate\_photo\_url VARCHAR(500),

&nbsp;   FOREIGN KEY (user\_id) REFERENCES users(user\_id) ON DELETE SET NULL,

&nbsp;   FOREIGN KEY (election\_id) REFERENCES elections(election\_id) ON DELETE CASCADE,

&nbsp;   INDEX idx\_election (election\_id)

);



-- 5. Create votes table (depends on users, elections, candidates)

CREATE TABLE votes (

&nbsp;   vote\_id BIGINT PRIMARY KEY AUTO\_INCREMENT,

&nbsp;   election\_id BIGINT NOT NULL,

&nbsp;   user\_id BIGINT NOT NULL,

&nbsp;   candidate\_id BIGINT NOT NULL,

&nbsp;   vote\_hash VARCHAR(255) UNIQUE NOT NULL,

&nbsp;   voted\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,

&nbsp;   FOREIGN KEY (election\_id) REFERENCES elections(election\_id) ON DELETE CASCADE,

&nbsp;   FOREIGN KEY (user\_id) REFERENCES users(user\_id) ON DELETE CASCADE,

&nbsp;   FOREIGN KEY (candidate\_id) REFERENCES candidates(candidate\_id) ON DELETE CASCADE,

&nbsp;   UNIQUE KEY unique\_vote\_per\_election (election\_id, user\_id),

&nbsp;   INDEX idx\_election\_votes (election\_id),

&nbsp;   INDEX idx\_voted\_at (voted\_at)

);



-- 6. Create voter\_election\_status table

CREATE TABLE voter\_election\_status (

&nbsp;   status\_id BIGINT PRIMARY KEY AUTO\_INCREMENT,

&nbsp;   election\_id BIGINT NOT NULL,

&nbsp;   user\_id BIGINT NOT NULL,

&nbsp;   has\_voted BOOLEAN DEFAULT FALSE,

&nbsp;   voted\_at TIMESTAMP NULL,

&nbsp;   FOREIGN KEY (election\_id) REFERENCES elections(election\_id) ON DELETE CASCADE,

&nbsp;   FOREIGN KEY (user\_id) REFERENCES users(user\_id) ON DELETE CASCADE,

&nbsp;   UNIQUE KEY unique\_voter\_election (election\_id, user\_id),

&nbsp;   INDEX idx\_election\_status (election\_id, has\_voted)

);



-- 7. Create election\_results table

CREATE TABLE election\_results (

&nbsp;   result\_id BIGINT PRIMARY KEY AUTO\_INCREMENT,

&nbsp;   election\_id BIGINT NOT NULL,

&nbsp;   candidate\_id BIGINT NOT NULL,

&nbsp;   vote\_count BIGINT DEFAULT 0,

&nbsp;   vote\_percentage DECIMAL(5,2),

&nbsp;   rank\_position INT,

&nbsp;   last\_updated TIMESTAMP DEFAULT CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP,

&nbsp;   FOREIGN KEY (election\_id) REFERENCES elections(election\_id) ON DELETE CASCADE,

&nbsp;   FOREIGN KEY (candidate\_id) REFERENCES candidates(candidate\_id) ON DELETE CASCADE,

&nbsp;   UNIQUE KEY unique\_election\_candidate (election\_id, candidate\_id),

&nbsp;   INDEX idx\_election\_results (election\_id, vote\_count DESC)

);



-- 8. Create election\_reports table

CREATE TABLE election\_reports (

&nbsp;   report\_id BIGINT PRIMARY KEY AUTO\_INCREMENT,

&nbsp;   election\_id BIGINT NOT NULL,

&nbsp;   total\_registered\_voters BIGINT DEFAULT 0,

&nbsp;   total\_votes\_cast BIGINT DEFAULT 0,

&nbsp;   voter\_turnout\_percentage DECIMAL(5,2),

&nbsp;   total\_candidates INT DEFAULT 0,

&nbsp;   winning\_candidate\_id BIGINT NULL,

&nbsp;   winning\_margin BIGINT NULL,

&nbsp;   report\_generated\_by BIGINT NOT NULL,

&nbsp;   report\_generated\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,

&nbsp;   FOREIGN KEY (election\_id) REFERENCES elections(election\_id) ON DELETE CASCADE,

&nbsp;   FOREIGN KEY (winning\_candidate\_id) REFERENCES candidates(candidate\_id) ON DELETE SET NULL,

&nbsp;   FOREIGN KEY (report\_generated\_by) REFERENCES users(user\_id) ON DELETE RESTRICT,

&nbsp;   INDEX idx\_election\_report (election\_id)

);



-- 9. Create dummy verification tables (no dependencies)

CREATE TABLE dummy\_aadhar\_records (

&nbsp;   aadhar\_id BIGINT PRIMARY KEY AUTO\_INCREMENT,

&nbsp;   aadhar\_number VARCHAR(12) UNIQUE NOT NULL,

&nbsp;   full\_name VARCHAR(255) NOT NULL,

&nbsp;   date\_of\_birth DATE NOT NULL,

&nbsp;   address TEXT,

&nbsp;   is\_valid BOOLEAN DEFAULT TRUE,

&nbsp;   created\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,

&nbsp;   INDEX idx\_aadhar (aadhar\_number)

);



CREATE TABLE dummy\_pan\_records (

&nbsp;   pan\_id BIGINT PRIMARY KEY AUTO\_INCREMENT,

&nbsp;   pan\_number VARCHAR(10) UNIQUE NOT NULL,

&nbsp;   full\_name VARCHAR(255) NOT NULL,

&nbsp;   date\_of\_birth DATE NOT NULL,

&nbsp;   is\_valid BOOLEAN DEFAULT TRUE,

&nbsp;   created\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,

&nbsp;   INDEX idx\_pan (pan\_number)

);



CREATE TABLE dummy\_voter\_id\_records (

&nbsp;   voter\_record\_id BIGINT PRIMARY KEY AUTO\_INCREMENT,

&nbsp;   voter\_id\_number VARCHAR(20) UNIQUE NOT NULL,

&nbsp;   full\_name VARCHAR(255) NOT NULL,

&nbsp;   date\_of\_birth DATE NOT NULL,

&nbsp;   is\_valid BOOLEAN DEFAULT TRUE,

&nbsp;   created\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,

&nbsp;   INDEX idx\_voter\_id (voter\_id\_number)

);



CREATE TABLE dummy\_passport\_records (

&nbsp;   passport\_record\_id BIGINT PRIMARY KEY AUTO\_INCREMENT,

&nbsp;   passport\_number VARCHAR(20) UNIQUE NOT NULL,

&nbsp;   full\_name VARCHAR(255) NOT NULL,

&nbsp;   date\_of\_birth DATE NOT NULL,

&nbsp;   is\_valid BOOLEAN DEFAULT TRUE,

&nbsp;   created\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,

&nbsp;   INDEX idx\_passport (passport\_number)

);



-- Insert sample data

INSERT INTO dummy\_aadhar\_records (aadhar\_number, full\_name, date\_of\_birth, address) VALUES

('123456789012', 'Rajesh Kumar', '1995-01-15', '123 MG Road, Mumbai, Maharashtra'),

('234567890123', 'Priya Sharma', '1990-05-20', '456 CP Avenue, Delhi'),

('345678901234', 'Amit Patel', '1988-08-10', '789 Brigade Road, Bangalore, Karnataka'),

('456789012345', 'Sneha Desai', '1992-03-25', '321 Park Street, Pune, Maharashtra'),

('567890123456', 'Vikram Singh', '1985-11-30', '654 Mall Road, Jaipur, Rajasthan');



INSERT INTO dummy\_pan\_records (pan\_number, full\_name, date\_of\_birth) VALUES

('ABCDE1234F', 'Rajesh Kumar', '1995-01-15'),

('BCDEF2345G', 'Priya Sharma', '1990-05-20'),

('CDEFG3456H', 'Amit Patel', '1988-08-10'),

('DEFGH4567I', 'Sneha Desai', '1992-03-25'),

('EFGHI5678J', 'Vikram Singh', '1985-11-30');



INSERT INTO dummy\_voter\_id\_records (voter\_id\_number, full\_name, date\_of\_birth) VALUES

('VOT1234567890', 'Rajesh Kumar', '1995-01-15'),

('VOT2345678901', 'Priya Sharma', '1990-05-20'),

('VOT3456789012', 'Amit Patel', '1988-08-10'),

('VOT4567890123', 'Sneha Desai', '1992-03-25'),

('VOT5678901234', 'Vikram Singh', '1985-11-30');



INSERT INTO dummy\_passport\_records (passport\_number, full\_name, date\_of\_birth) VALUES

('P12345678', 'Rajesh Kumar', '1995-01-15'),

('P23456789', 'Priya Sharma', '1990-05-20'),

('P34567890', 'Amit Patel', '1988-08-10'),

('P45678901', 'Sneha Desai', '1992-03-25'),

('P56789012', 'Vikram Singh', '1985-11-30');

