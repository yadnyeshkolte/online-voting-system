# Testing Instructions

This guide explains how to run and test the Online Voting System on your local machine.

## Prerequisites

*   **Java 21** or later
*   **MySQL Server** (running on localhost:3306)
*   **Node.js** (v18 or later)

## Database Setup

1.  Make sure your MySQL server is running.
2.  The application requires a database named `devovs`.
3.  The application is configured to create the database if it doesn't exist (`createDatabaseIfNotExist=true`), but it's good practice to ensure it exists.
4.  The application uses `spring.jpa.hibernate.ddl-auto=update`, so it will create the tables automatically based on the Entity classes.
5.  **Important:** You need to populate the **Dummy Verification Tables** manually or via a script if you want to test registration verification properly. You can use the `INSERT` statements provided in `docs/database.md` (section 9).

## Running the Backend

1.  Open a terminal (Command Prompt or PowerShell) and navigate to the `backend/onlinevotingsystem` directory.
2.  Run the application using Maven Wrapper:
    ```bash
    ./mvnw spring-boot:run
    ```
    (On Windows Command Prompt, just `mvnw spring-boot:run`)
3.  The backend will start on `http://localhost:8080`.

## Running the Frontend

1.  Open another terminal and navigate to the `frontend` directory.
2.  Install dependencies (if you haven't already):
    ```bash
    npm install
    ```
3.  Start the development server:
    ```bash
    npm run dev
    ```
4.  The frontend will usually start on `http://localhost:5173`.

## Testing Flow

### 1. User Registration (Verification)
1.  Go to `http://localhost:5173/register`.
2.  Fill in the details.
3.  **Crucial Step:** For "ID Proof Number", you **must** use one of the values from the dummy records (e.g., `123456789012` for Aadhar) or insert your own valid record into the database first.
    *   Example Aadhar: `123456789012` (Name: Rajesh Kumar)
4.  If the ID matches a record in the database, registration is successful.

### 2. Admin Setup (First Time)
Since there is no "Sign Up as Admin" page (security risk), you need to manually promote a user to Admin in the database or use the API.
1.  Register a user (e.g., `admin@test.com`).
2.  Access the database directly (via Workbench or CLI) and run:
    ```sql
    UPDATE users SET role = 'ADMIN' WHERE email = 'admin@test.com';
    INSERT INTO admins (user_id) SELECT user_id FROM users WHERE email = 'admin@test.com';
    ```
    *Alternatively, you can call the API endpoint `/api/admin/make-admin/{userId}` if you disable security temporarily, but DB is safer.*

### 3. Admin Dashboard
1.  Login with the Admin account at `http://localhost:5173/login`.
2.  Navigate to `http://localhost:5173/admin`.
3.  **Create Election:** Click "Create New Election", set dates, type, and name.
4.  **Manage Candidates:** Click "Candidates" for the created election and add candidates (Party Name, Symbol).
5.  **Start Election:** Change status from DRAFT -> SCHEDULED -> ACTIVE.

### 4. Voting (User)
1.  Open an Incognito window or logout.
2.  Login with a Voter account (different from Admin).
3.  Navigate to `http://localhost:5173/dashboard`.
4.  You should see the "Active" election.
5.  Click "Vote Now", select a candidate, and submit.
6.  You will see a "Voted" status.

### 5. Results
1.  Login as Admin again.
2.  Go to Dashboard.
3.  End the election (ACTIVE -> COMPLETED).
4.  Click "Calculate Results".
5.  Click "View Results" to see the winner and counts.

## Troubleshooting
*   **CORS Errors:** If you see CORS errors in the browser console, ensure the backend `SecurityConfig` allows `http://localhost:5173` (currently configured to allow all `*`).
*   **Database Connection:** Check `backend/onlinevotingsystem/src/main/resources/application.properties` if you have different MySQL credentials (default: root/password).
