---
title: "Admin Dashboard"
permalink: /docs/frontend/admin-dashboard/
excerpt: "Administrative controls and election management"
last_modified_at: 2026-01-21
toc: true
---

# Admin Dashboard

The Admin Dashboard is a protected area restricted to users with the `ADMIN` role. It provides comprehensive control over elections and users.

## Manage Elections

The **Manage Elections** tab is the primary workspace for election lifecycle management.

### Election Lifecycle
Admins control the state of an election through the following buttons:
1.  **Schedule**: Transitions `DRAFT` -> `SCHEDULED`.
2.  **Start**: Transitions `SCHEDULED` -> `ACTIVE`.
3.  **End**: Transitions `ACTIVE` -> `COMPLETED`.

### Creating an Election
The "Create New Election" modal allows admins to:
*   Define election metadata (Name, Type, Dates, Location).
*   **Add Candidates**:
    *   Candidates are added to a temporary list before election creation.
    *   **Image Upload**: Admins upload candidate photos directly during this phase.
    *   The system handles the complex flow of creating the election entity first, then uploading images for each candidate.

### Results Management
*   **Calculate Results**: Triggers the backend calculation engine.
*   **View Results**: Displays the current vote tallies.
*   **Publish/Unpublish**: Controls whether results are visible to voters on their dashboard.

## Manage Users

The **Manage Users** tab provides a searchable interface for the user database.

*   **Search**: Admins can search by Name, ID, Email, Phone, Aadhar, Voter ID, PAN, or Passport.
*   **Edit**: Opens the `EditUserModal` to modify user details. Critical identity fields (Aadhar, Voter ID) are locked to prevent tampering.

## Code Structure

*   `AdminDashboard.jsx`: Main container, handles Election state and tab switching.
*   `ManageUsers.jsx`: Sub-component specifically for the User search and edit table.
