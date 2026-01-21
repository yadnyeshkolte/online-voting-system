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
        "title": "Logins & Authentication",
        "excerpt":"Authentication &amp; Login The Online Voting System uses a secure JWT (JSON Web Token) based authentication mechanism. Login Flow User Submission: Client sends POST /api/auth/login with email and password. Verification: System looks up user by email. Compares submitted password with stored BCrypt hash. Token Generation: If valid, server generates a...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/backend/logins/",
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
        "title": "Database Schema",
        "excerpt":"Complete Schema Reference SET SQL_SAFE_UPDATES = 0; CREATE DATABASE IF NOT EXISTS devovs; USE devovs; -- ============================================ -- ONLINE VOTING SYSTEM - FINAL SCHEMA -- PG-DAC Project - Group 06 -- ============================================ -- 1. Create users table first (no dependencies) -- Users can be both VOTERS and CANDIDATES -- Modified...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/database/databaseschema/",
        "teaser": null
      },{
        "title": "Election Insertion Queries",
        "excerpt":"Election Data Insertion -- 3. INSERT 2 ACTIVE ELECTIONS AND 2 PAST ELECTIONS INSERT INTO elections (election_name, election_type, start_date, end_date, status, result_published, result_published_at, result_published_by, created_by) VALUES -- Active Elections ('Maharashtra State Assembly Election 2025', 'STATE', '2025-11-20 08:00:00', '2025-11-25 18:00:00', 'ACTIVE', FALSE, NULL, NULL, 1), ('Mumbai Municipal Corporation Election 2025', 'LOCAL',...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/database/electioninsertion/",
        "teaser": null
      },{
        "title": "User Insertion Queries",
        "excerpt":"User Data Insertion -- 2. INSERT 2 ADMINS (Passwords set to 'admin123', email updated to match login) INSERT INTO admins (email, password_hash, full_name, phone_number) VALUES ('admin@voting.com', '$2a$12$8HNsrESl9ubVI9MRaCFWY.vZZ1i6OkpmzaTnz.5nsdTGdeWEzzhym', 'Sanjay Malhotra', '9999888877'), ('admin.secondary@voting.com', '$2a$12$8HNsrESl9ubVI9MRaCFWY.vZZ1i6OkpmzaTnz.5nsdTGdeWEzzhym', 'Priyanka Chopra', '9999888878'); -- 7. UPDATE dummy verification tables with the 20 inserted users -- Insert verification records...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/database/userinsertion/",
        "teaser": null
      },{
        "title": "Diagrams",
        "excerpt":"ER Diagram Part 1      ER Diagram Part 2      ER Diagram Part 3      Level 0 DFD (Data Flow Diagram)      Level 1 DFD (Data Flow Diagram)      Deployment Diagram      Activity Diagram      System Diagrams      ","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/diagrams/",
        "teaser": null
      },{
        "title": "Admin Dashboard",
        "excerpt":"Admin Dashboard The Admin Dashboard is a protected area restricted to users with the ADMIN role. It provides comprehensive control over elections and users. Manage Elections The Manage Elections tab is the primary workspace for election lifecycle management. Election Lifecycle Admins control the state of an election through the following...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/frontend/admin-dashboard/",
        "teaser": null
      },{
        "title": "Authentication",
        "excerpt":"Authentication Flows The application supports distinct login flows for Users and Administrators, along with a comprehensive registration process. Login Pages User Login (/login): Standard email/password login for voters. Admin Login (/admin-login): Dedicated login portal for system administrators. Both forms use the AuthContext to store the received JWT and user details...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/frontend/authentication/",
        "teaser": null
      },{
        "title": "UI Components",
        "excerpt":"UI Components &amp; Architecture The frontend is built using React and Vite, utilizing React-Bootstrap for responsive styling and pre-built components. Core Libraries React Bootstrap: Grid system (Container, Row, Col), Modals, Buttons, and Cards. React Router DOM: Client-side routing. React Webcam: Handling camera inputs for voter verification. Axios: HTTP client for...","categories": [],
        "tags": [],
        "url": "/online-voting-system/docs/frontend/ui-components/",
        "teaser": null
      },{
        "title": "User Dashboard & Voting",
        "excerpt":"User Dashboard &amp; Voting The User Dashboard is the central hub for voters. It allows users to view active elections, cast their votes securely using face verification, and view results of past elections. Dashboard Overview The dashboard is divided into two main sections: Active Elections: Elections currently open for voting....","categories": [],
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
