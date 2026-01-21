var store = [{
        "title": "API Reference",
        "excerpt":"This document provides a reference for the RESTful API endpoints available in the Online Voting System. The API is divided into public (Authentication, Verification) and protected (Admin, User) sections. Authentication &amp; Verification Base URL: /api/auth Method Endpoint Description Request Body POST /login Authenticate a user or admin. LoginRequest (email, password)...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/backend/api-reference/",
        "teaser": null
      },{
        "title": "Authentication",
        "excerpt":"Authentication Overview OVS uses JWT (JSON Web Tokens) for authentication and role-based access control. Authentication Flow User submits credentials Server validates credentials Server generates JWT token Client stores token Client includes token in subsequent requests JWT Token Structure eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9. eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwicm9sZSI6InZvdGVyIiwiaWF0IjoxNjk4NzYwMDAwLCJleHAiOjE2OTg3NjM2MDB9. signature User Roles Admin: Full system access Voter: Can view...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/backend/authentication/",
        "teaser": null
      },{
        "title": "Backend Overview",
        "excerpt":"The Online Voting System backend is a robust and secure application designed to manage the core logic of the voting platform. Built with modern Java technologies, it ensures data integrity, security, and high performance for elections. Key Features Secure Authentication: Uses JWT (JSON Web Tokens) for stateless, secure user and...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/backend/backend-overview/",
        "teaser": null
      },{
        "title": "Backend Structure",
        "excerpt":"The Online Voting System backend follows a standard Spring Boot layered architecture, promoting separation of concerns and maintainability. The source code is located under src/main/java/com/project/onlinevotingsystem. Package Organization config Contains configuration classes for the application context. SecurityConfig.java: Configures Spring Security, defining filter chains, public/private endpoints, and CORS settings. JwtRequestFilter.java: A filter...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/backend/backend-structure/",
        "teaser": null
      },{
        "title": "Security Authentication",
        "excerpt":"The Online Voting System employs a robust security model to ensure the integrity of elections and the privacy of user data. The system uses Spring Security combined with JWT (JSON Web Tokens) for stateless authentication. Authentication Mechanism JWT (JSON Web Token) The system uses JWTs to secure API endpoints. Login:...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/backend/security-auth/",
        "teaser": null
      },{
        "title": "Spring Container",
        "excerpt":"Spring Container Overview The Online Voting System backend uses Spring Boot and its powerful IoC (Inversion of Control) container for dependency management. Application Structure backend/ ├── src/ │ ├── main/ │ │ ├── java/ │ │ │ └── com/ovs/ │ │ │ ├── OvsApplication.java │ │ │ ├── config/ │...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/backend/springcontainer/",
        "teaser": null
      },{
        "title": "Database ER Diagram",
        "excerpt":"erDiagram admins ||--o{ elections : \"creates\" admins ||--o{ elections : \"publishes_results\" admins ||--o{ election_reports : \"generates\" users ||--o{ candidates : \"can_be\" users ||--o{ votes : \"casts\" users ||--o{ voter_election_status : \"has\" elections ||--o{ candidates : \"has\" elections ||--o{ votes : \"receives\" elections ||--o{ voter_election_status : \"tracks\" elections ||--o{ election_results :...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/database/databaseer/",
        "teaser": null
      },{
        "title": "Database Insertion Queries",
        "excerpt":"-- ============================================ -- TEST DATA INSERTION -- ============================================ -- 1. INSERT 20 USERS WITH BCRYPT HASHED PASSWORDS -- Updated to include both AADHAR and VOTER_ID for each user INSERT INTO users (email, password_hash, full_name, phone_number, date_of_birth, gender, address, city, state, pincode, aadhar_number, voter_id_number, profile_image_url, is_active, is_verified, approved_at) VALUES ('rajesh.kumar@email.com', '$2a$12$h2bW9dGj8X89JMBBHjvffe2jKCerIAzPFK.LsQiy0yV3fOk/NwiV6',...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/database/databaseinsertion/",
        "teaser": null
      },{
        "title": "Database Insertion Queries 2",
        "excerpt":"-- ============================================ -- MISSING DATA FOR KARNATAKA AND DELHI ELECTIONS -- ============================================ -- 4. INSERT CANDIDATES FOR ELECTION 3 (Karnataka) AND ELECTION 4 (Delhi) -- Candidates for Election 3 (Karnataka State Assembly Election 2024) -- IDs will be 11, 12, 13, 14, 15 INSERT INTO candidates (user_id, election_id, party_name, party_symbol,...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/database/databaseinsertion2/",
        "teaser": null
      },{
        "title": "Database Structure",
        "excerpt":"Complete Schema Reference CREATE DATABASE IF NOT EXISTS devovs; USE devovs; -- ============================================ -- ONLINE VOTING SYSTEM - FINAL SCHEMA -- PG-DAC Project - Group 06 -- ============================================ -- 1. Create users table first (no dependencies) -- Users can be both VOTERS and CANDIDATES -- Modified to include specific Aadhar...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/database/databasestructure/",
        "teaser": null
      },{
        "title": "OVS Database",
        "excerpt":"This document outlines the database schema for this application. TABLE 1: ADMINS FIELD ID FIELD NAME DATA TYPE CONSTRAINTS 1 admin_id bigint PRIMARY KEY, AUTO_INCREMENT 2 user_id bigint FOREIGN KEY, UNIQUE 3 assigned_at timestamp NOT NULL TABLE 2: ELECTION_RESULT FIELD ID FIELD NAME DATA TYPE CONSTRAINTS 1 result_id bigint PRIMARY...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/database/ovs-database/",
        "teaser": null
      },{
        "title": "Diagrams",
        "excerpt":"ER Diagram Part 1      ER Diagram Part 2      ER Diagram Part 3      Level 0 DFD (Data Flow Diagram)      Level 1 DFD (Data Flow Diagram)      Deployment Diagram      Activity Diagram      System Diagrams      ","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/diagrams/",
        "teaser": null
      },{
        "title": "Feedback System",
        "excerpt":"Feedback System Overview The feedback system allows users to provide feedback about their voting experience and report issues. Feedback Types General Feedback: Overall experience Technical Issues: Bugs or errors Feature Requests: Suggestions for improvement Usability: UI/UX feedback Feedback Component import React, { useState } from 'react'; function FeedbackForm() { const...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/frontend/feedback/",
        "teaser": null
      },{
        "title": "UI Components",
        "excerpt":"UI Components Component Library OVS uses a custom component library built with React. Core Components Button &lt;Button variant=\"primary\" size=\"large\" onClick={handleClick} &gt; Cast Vote &lt;/Button&gt; Card &lt;Card&gt; &lt;CardHeader&gt; &lt;h3&gt;Election Title&lt;/h3&gt; &lt;/CardHeader&gt; &lt;CardBody&gt; &lt;p&gt;Election description...&lt;/p&gt; &lt;/CardBody&gt; &lt;CardFooter&gt; &lt;Button&gt;View Details&lt;/Button&gt; &lt;/CardFooter&gt; &lt;/Card&gt; Modal &lt;Modal isOpen={isOpen} onClose={handleClose}&gt; &lt;ModalHeader&gt;Confirm Vote&lt;/ModalHeader&gt; &lt;ModalBody&gt; Are you sure you...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/frontend/ui-components/",
        "teaser": null
      },{
        "title": "User Effect Voting",
        "excerpt":"User Effect Voting Overview The voting interface provides an intuitive and secure way for users to cast their votes. Voting Flow User logs in to the system Views available elections Selects an election Reviews candidates Casts vote Receives confirmation UI Components Election List import React, { useEffect, useState } from...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/frontend/usereffectvoting/",
        "teaser": null
      },{
        "title": "Installation",
        "excerpt":"Installation Guide Follow these steps to set up the Online Voting System on your local machine. Prerequisites Ensure you have the following software installed: Java 21: Required for the backend. Node.js (v18+): Required for the frontend. MySQL: Required for the database. Git: To clone the repository. 1. Database Setup Open...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/installation/",
        "teaser": null
      },{
        "title": "Introduction",
        "excerpt":"About Online Voting System The Online Voting System is a secure and reliable platform designed for conducting online elections. Built with modern web technologies, it ensures transparency, security, and ease of use for both voters and administrators. The system addresses the need for a digital voting solution that guarantees: Secure...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/introduction/",
        "teaser": null
      },{
        "title": "Quick Start",
        "excerpt":"Quick Start Guide This guide will help you verify the application flow by simulating a real-world voting scenario. Prerequisites Ensure you have completed the Installation Guide and both the Backend and Frontend servers are running. Frontend: http://localhost:5173 Backend: http://localhost:8080 Walkthrough 1. Admin: Create an Election Navigate to http://localhost:5173/login. Login with...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/quick-start/",
        "teaser": null
      },]
