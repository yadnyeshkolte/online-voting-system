---
title: "UI Components"
permalink: /docs/frontend/ui-components/
excerpt: "Frontend UI Architecture"
last_modified_at: 2026-01-21
toc: true
---

# UI Components & Architecture

The frontend is built using **React** and **Vite**, utilizing **React-Bootstrap** for responsive styling and pre-built components.

## Core Libraries

*   **React Bootstrap**: Grid system (Container, Row, Col), Modals, Buttons, and Cards.
*   **React Router DOM**: Client-side routing.
*   **React Webcam**: Handling camera inputs for voter verification.
*   **Axios**: HTTP client for API communication.

## Custom Components

### `EditUserModal`

A specialized modal component used by Administrators to update user details.

*   **Props**: `show`, `onHide`, `user`, `onUpdate`
*   **Features**:
    *   Pre-fills form with user data.
    *   ReadOnly fields for immutable identity data (Aadhar, Voter ID).
    *   Conditional editing for fields like PAN/Passport (allows addition if missing).

### `PrivateRoute`

A Higher-Order Component (HOC) that protects routes based on authentication status and user roles (`ADMIN` vs `USER`).

```jsx
<Route
  path="/admin"
  element={
    <PrivateRoute role="ADMIN">
      <AdminDashboard />
    </PrivateRoute>
  }
/>
```

### `Navigation`

The main app navbar that conditionally renders links based on the logged-in user's role (e.g., hiding "Admin Dashboard" link from standard users).

## Styling Strategy

The application uses standard CSS imports (`App.css`, `index.css`) alongside Bootstrap utility classes (e.g., `mt-4`, `text-center`, `text-muted`) for rapid UI development.