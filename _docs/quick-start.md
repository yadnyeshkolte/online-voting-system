---
title: "Quick Start"
permalink: /docs/quick-start/
excerpt: "Get up and running quickly"
last_modified_at: 2025-11-15
toc: true
---

# Quick Start Guide

This guide will help you verify the application flow by simulating a real-world voting scenario.

## Prerequisites
Ensure you have completed the [Installation Guide](INSTALLATION.md) and both the Backend and Frontend servers are running.

*   **Frontend**: http://localhost:5173
*   **Backend**: http://localhost:8080

## Walkthrough

### 1. Admin: Create an Election

1.  Navigate to **[http://localhost:5173/login](http://localhost:5173/login)**.
2.  Login with the default Admin credentials:
    *   **Email**: `admin@voting.com`
    *   **Password**: `admin123`
3.  On the **Admin Dashboard**:
    *   Click "Create Election".
    *   Enter details (e.g., "General Election 2024").
    *   Change the election status to `SCHEDULED`, then `ACTIVE` to allow voting.
    *   *Note: To add candidates, you must first register other users and use their User IDs.*

### 2. User: Register & Verify

1.  Log out or open an Incognito window.
2.  Navigate to **[http://localhost:5173/register](http://localhost:5173/register)**.
3.  Fill in the registration form using the **Seeded Dummy Data** for verification.
4.  Click **Register**. If the details match the dummy data, registration will succeed.

### 3. User: Vote

1.  Login with your newly created user credentials.
2.  On the **User Dashboard**, locate the Active Election you created earlier.
3.  Click **Vote Now**.
4.  Select a candidate and confirm your vote.
5.  The button will update to "You Voted".

### 4. Admin: View Results

1.  Log back in as Admin.
2.  Navigate to the election card on the dashboard.
3.  Click **Calc Results** (if required) then **View Results**.
4.  You will see the updated vote counts.

---

## Default Accounts

| Role | Email | Password |
| :--- | :--- | :--- |
| **Admin** | `admin@voting.com` | `admin123` |
| **Admin (Backup)** | `admin.secondary@voting.com` | `admin123` |
