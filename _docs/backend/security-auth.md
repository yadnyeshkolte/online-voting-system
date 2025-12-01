---
title: "Security Authentication"
permalink: /docs/backend/security-auth/
excerpt: "Overview for Backend Structure"
last_modified_at: 2025-12-01
toc: true
---

The Online Voting System employs a robust security model to ensure the integrity of elections and the privacy of user data. The system uses **Spring Security** combined with **JWT (JSON Web Tokens)** for stateless authentication.

## Authentication Mechanism

### JWT (JSON Web Token)
The system uses JWTs to secure API endpoints.
1.  **Login**: Users verify their credentials (email/password) via the `/api/auth/login` endpoint.
2.  **Token Generation**: Upon successful authentication, the server generates a signed JWT containing:
    -   **Subject**: The user's email (username).
    -   **Claims**: `role` (ADMIN or USER), `id` (User/Admin ID), `issuedAt`, `expiration`.
    -   **Signature**: HMAC-SHA256 signature using a secret key.
3.  **Token Usage**: The client must send this token in the `Authorization` header (`Bearer <token>`) for all subsequent requests to protected endpoints.
4.  **Validation**: The `JwtRequestFilter` intercepts every request, extracts the token, verifies the signature and expiration, and sets the `Authentication` context if valid.

### Password Storage
Passwords are strictly **hashed** before storage using `BCryptPasswordEncoder`. Plain text passwords are never stored in the database.

## Role-Based Access Control (RBAC)

The system defines two primary roles:

### 1. ADMIN
-   **Access**: Full access to `/api/admin/**` endpoints.
-   **Capabilities**: Create elections, manage candidates, generate reports, view all users, update system data.
-   **Identification**: Checks against the `admins` table.

### 2. USER
-   **Access**: Restricted to `/api/user/**` endpoints.
-   **Capabilities**: Manage own profile, view active/past elections, cast votes (one per election).
-   **Identification**: Checks against the `users` table.

## Identity Verification

To ensure one-person-one-vote and prevent fraud, the registration process includes a strict identity verification step handled by the `VerificationService`.

### Verification Logic
Before a user account is created, the system validates the provided details against "Dummy" government record tables (`dummy_aadhar_records`, `dummy_voter_id_records`).
-   **Aadhar Verification**: Matches Number + Full Name + DOB.
-   **Voter ID Verification**: Matches Number + Full Name + DOB.
-   **Uniqueness**: The system also checks if the Aadhar or Voter ID has already been used to register an existing account.

Only if both checks pass is the user allowed to register.

## CORS Configuration

Cross-Origin Resource Sharing (CORS) is configured to allow requests from the frontend application (typically running on `http://localhost:5173`).
-   Allowed Methods: GET, POST, PUT, DELETE, OPTIONS.
-   Allowed Headers: Authorization, Content-Type.
