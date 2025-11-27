---
title: "Installation"
permalink: /docs/installation/
excerpt: "How to install Online Voting System"
last_modified_at: 2025-11-15
toc: true
---

# Installation Guide

Follow these steps to set up the Online Voting System on your local machine.

## Prerequisites

Ensure you have the following software installed:

1.  **Java 21**: Required for the backend.
2.  **Node.js (v18+)**: Required for the frontend.
3.  **MySQL**: Required for the database.
4.  **Git**: To clone the repository.

## 1. Database Setup

1.  Open your MySQL client (e.g., MySQL Workbench, command line).
2.  Create a new database named `devovs`:

    ```sql
    CREATE DATABASE devovs;
    ```

3.  **Configure Credentials**:
    The backend connects to the database using the credentials defined in `backend/onlinevotingsystem/src/main/resources/application.properties`.
    
    *   **Default Username**: `root`
    *   **Default Password**: `password`
    
    If your local MySQL password differs, open `backend/onlinevotingsystem/src/main/resources/application.properties` and update the `spring.datasource.password` field.

## 2. Backend Setup

1.  Navigate to the backend directory:
    ```bash
    cd backend/onlinevotingsystem
    ```

2.  Run the application using the Maven wrapper:
    *   **Linux/Mac**:
        ```bash
        ./mvnw spring-boot:run
        ```
    *   **Windows**:
        ```bash
        mvnw spring-boot:run
        ```

    *The backend server will start on port `8080`.*

## 3. Frontend Setup

1.  Open a new terminal window.
2.  Navigate to the frontend directory:
    ```bash
    cd frontend
    ```

3.  Install dependencies:
    ```bash
    npm install
    ```

4.  Start the development server:
    ```bash
    npm run dev
    ```

    *The frontend will be accessible at `http://localhost:5173`.*

## Troubleshooting

*   **Port Conflicts**: Ensure ports `8080` (Backend) and `5173` (Frontend) are free.
*   **Database Connection**: If the backend fails to start, double-check your MySQL username and password in `application.properties` and ensure the `devovs` database exists.
*   **Permission Denied (Linux/Mac)**: If `./mvnw` is not executable, run `chmod +x mvnw`.
