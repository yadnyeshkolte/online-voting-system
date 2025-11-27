---
title: "Introduction"
permalink: /docs/introduction/
excerpt: "Introduction to Online Voting System"
last_modified_at: 2025-11-15
toc: true
---

## About Online Voting System

The **Online Voting System** is a secure and reliable platform designed for conducting online elections. Built with modern web technologies, it ensures transparency, security, and ease of use for both voters and administrators.

The system addresses the need for a digital voting solution that guarantees:
1. **Secure Web-Based Platform** for online voting and real-time result display.
2. **Admin Management System** to efficiently manage elections, candidates, and voters.
3. **Modern Architecture** built using React.js, Spring Boot, and MySQL with secure authentication.
4. **Transparency & Security** ensuring fair elections with audit trails and vote integrity.

---

## 📋 Project Details

- **Course Name**: PG-DAC  
- **Batch Name**: August 2025  
- **Group Number**: 06  
- **Project Title**: Online Voting System

---

## 🎯 Key Features

### For Voters
- ✅ **Secure Registration** - JWT-based authentication system
- ✅ **Easy Voting** - Simple and intuitive voting interface
- ✅ **Real-time Results** - Live vote counting and results display
- ✅ **Vote Verification** - Check voting status and history

### For Administrators
- ✅ **Election Management** - Create and manage elections
- ✅ **Candidate Management** - Add and manage candidates
- ✅ **Voter Management** - User verification and approval
- ✅ **Results Dashboard** - Comprehensive election reports
- ✅ **Audit Trail** - Complete voting history and logs

### Security Features
- 🔒 **Encrypted Passwords** - BCrypt hashing
- 🔒 **Vote Integrity** - Cryptographic hash for each vote
- 🔒 **One Vote Policy** - Database constraint enforcement
- 🔒 **ID Verification** - Aadhar, PAN, Voter ID, Passport support
- 🔒 **Role-Based Access** - Separate admin and voter privileges

---

## 💻 Technology Stack

### Frontend
- **React.js** - Modern UI framework
- **HTML5 & CSS3** - Responsive design
- **JavaScript (ES6+)** - Interactive components

### Backend
- **Spring Boot** - Java-based backend framework
- **Spring Security** - Authentication and authorization
- **JWT** - Secure token-based authentication
- **Spring Data JPA** - Database operations

### Database
- **MySQL 8.0+** - Relational database management

### Development Tools
- **IntelliJ IDEA / Eclipse** - Java development
- **Visual Studio Code** - Frontend development
- **Git & GitHub** - Version control
- **Maven** - Dependency management
- **npm** - Package management

---

## 🏗️ Project Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    ONLINE VOTING SYSTEM                 │
└─────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
   ┌─────────┐        ┌──────────┐       ┌──────────┐
   │ React.js│◄──────►│  Spring  │◄─────►│  MySQL   │
   │Frontend │        │   Boot   │       │ Database │
   └─────────┘        └──────────┘       └──────────┘
        │                   │                   │
    UI Layer          Business Logic      Data Storage
    Components        REST APIs            Relationships
    State Mgmt        JWT Auth             Transactions
```

---

## 👥 Team Members

| Sr. No | Name | PRN | GitHub |
|--------|------|-----|--------|
| 01 | Aaman Javaed Sayyad | 250850120003 | [@Aamanjs](https://github.com/Aamanjs) |
| 02 | Avadhut Ravindra Joshi | 250850120042 | [@AvadhutJoshi012](https://github.com/AvadhutJoshi012) |
| 03 | Deepak Sanjay Revgade | 250850120137 | [@deepakrevgade](https://github.com/deepakrevgade) |
| 04 | Rishikesh Sukhadev More | 250850120143 | [@rushimore17](https://github.com/rushimore17) |
| 05 | Yadnyesh Rajesh Kolte | 250850120192 | [@yadnyeshkolte](https://github.com/yadnyeshkolte) |
