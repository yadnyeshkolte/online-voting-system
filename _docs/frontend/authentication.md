---
title: "Authentication"
permalink: /docs/frontend/authentication/
excerpt: "Frontend Authentication Flows"
last_modified_at: 2026-01-21
toc: true
---

# Authentication Flows

The application supports distinct login flows for Users and Administrators, along with a comprehensive registration process.

## Login Pages

*   **User Login** (`/login`): Standard email/password login for voters.
*   **Admin Login** (`/admin-login`): Dedicated login portal for system administrators.

Both forms use the `AuthContext` to store the received JWT and user details upon successful authentication.

## Registration

The Registration page (`/register`) is a multi-step form that captures:
1.  **Personal Details**: Name, Email, Phone, DOB, Gender.
2.  **Address**: Full address details.
3.  **Identity Verification**:
    *   **Aadhar Number**
    *   **Voter ID Number**
    *   **Profile Photo**: Users must provide a photo for future face verification.

## Route Protection

The `PrivateRoute` component ensures security by checking:
1.  **Is Authenticated**: Is there a valid JWT?
2.  **Role Authorization**: Does the user have the required role (`USER` or `ADMIN`) for the requested route?

If checks fail, the user is redirected to the appropriate login page.
