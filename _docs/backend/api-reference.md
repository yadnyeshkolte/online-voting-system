---
title: "API Reference"
permalink: /docs/backend/api-reference/
excerpt: "Complete API documentation"
last_modified_at: 2025-12-01
toc: true
---

This document provides a reference for the RESTful API endpoints available in the Online Voting System. The API is divided into public (Authentication, Verification) and protected (Admin, User) sections.

## Authentication & Verification
Base URL: `/api/auth`

| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| `POST` | `/login` | Authenticate a user or admin. | `LoginRequest` (email, password) |
| `POST` | `/register` | Register a new user (requires identity verification). | `RegisterRequest` (details + Aadhar/VoterID) |

**Note**: Identity verification is handled internally during registration via `VerificationService`.

## Admin Endpoints
Base URL: `/api/admin`
**Requires Role**: `ADMIN`

### Election Management (`/elections`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/` | Get a list of all elections. |
| `POST` | `/` | Create a new election. |
| `PUT` | `/{id}/status` | Update the status of an election (e.g., ACTIVE, COMPLETED). |
| `POST` | `/{id}/candidates` | Add a candidate to a DRAFT election. |
| `POST` | `/{id}/calculate-results` | Trigger result calculation for an election. |
| `GET` | `/{id}/results` | Get results for a specific election. |
| `POST` | `/{id}/candidates/{candidateId}/photo` | Upload a photo for a candidate. |

### User Management (`/users`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/users` | Search for users (optional `query` param). |
| `PUT` | `/users/{userId}` | Update a user's details as an admin. |
| `PUT` | `/candidates/{candidateId}/image` | Update a candidate's image (alternative endpoint). |

## User Endpoints
Base URL: `/api/user`
**Requires Role**: `USER`

### Profile Management (`/profile`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/profile` | Get the authenticated user's profile. |
| `PUT` | `/profile` | Update the user's profile information. |
| `POST` | `/profile/photo` | Upload a profile photo. |
| `GET` | `/profile/photo/{filename}` | Serve a profile photo (Public access allowed for resources). |

### Election & Voting (`/elections`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/active` | Get a list of active elections. |
| `GET` | `/completed` | Get a list of completed elections (past elections). |
| `GET` | `/{id}/candidates` | Get the list of candidates for a specific election. |
| `POST` | `/{id}/vote` | Cast a vote for a candidate. |
| `GET` | `/{id}/has-voted` | Check if the current user has voted in a specific election. |
| `GET` | `/{id}/results` | Get results for a completed election. |
| `GET` | `/candidates/{candidateId}/photo` | Get a candidate's photo. |

## Error Handling

The API generally returns standard HTTP status codes:
-   `200 OK`: Success.
-   `400 Bad Request`: Invalid input or validation failure.
-   `403 Forbidden`: Insufficient permissions or accessing restricted resources (e.g., results of an active election).
-   `404 Not Found`: Resource not found.
-   `500 Internal Server Error`: Server-side issues.
