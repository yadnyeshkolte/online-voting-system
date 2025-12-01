---
title: "Backend Overview"
permalink: /docs/backend/backend-overview/
excerpt: "Here is our Backend Overview"
last_modified_at: 2025-12-01
toc: true
---

The Online Voting System backend is a robust and secure application designed to manage the core logic of the voting platform. Built with modern Java technologies, it ensures data integrity, security, and high performance for elections.

## Key Features

-   **Secure Authentication**: Uses JWT (JSON Web Tokens) for stateless, secure user and admin authentication.
-   **Role-Based Access Control (RBAC)**: Distinct permissions for `ADMIN` and `USER` roles.
-   **Identity Verification**: Integrated verification system that validates users against dummy Aadhar and Voter ID records before registration.
-   **Election Management**: Comprehensive tools for admins to create, manage, and monitor elections.
-   **Real-time Results**: Automated calculation of election results and generation of reports.
-   **Candidate Management**: Functionality to manage candidates, including profile photos and details.
-   **Audit Trails**: Records of who created elections and generated reports.

## Technology Stack

The backend is built using the following technologies:

-   **Java**: Version 21
-   **Spring Boot**: Version 4.0.0
-   **Build Tool**: Maven
-   **Database**: MySQL (Database name: `devovs`)
-   **Persistence**: Spring Data JPA / Hibernate
-   **Security**: Spring Security, JJWT (Java JWT)
-   **Utilities**: Lombok

## Configuration

The application is configured via `application.properties`. Key configurations include:

### Database
-   **URL**: `jdbc:mysql://localhost:3306/devovs`
-   **Username**: `root`
-   **Password**: `982223` (Note: Default development password)
-   **DDL Auto**: `update` (Schema is automatically updated)

### Security
-   **JWT Secret**: Configured via `jwt.secret` property.
-   **JWT Expiration**: Defaults to 24 hours (`86400000` ms).

### Server
-   **Port**: `8080`

## Getting Started

The main entry point for the application is `com.project.onlinevotingsystem.OnlinevotingsystemApplication`. Standard Spring Boot commands can be used to run the application:

```bash
./mvnw spring-boot:run
```
