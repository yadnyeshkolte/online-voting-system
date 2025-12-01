---
title: "Backend Structure"
permalink: /docs/backend/backend-structure/
excerpt: "Overview for Backend Structure"
last_modified_at: 2025-12-01
toc: true
---

The Online Voting System backend follows a standard Spring Boot layered architecture, promoting separation of concerns and maintainability. The source code is located under `src/main/java/com/project/onlinevotingsystem`.

## Package Organization

### `config`
Contains configuration classes for the application context.
-   **`SecurityConfig.java`**: Configures Spring Security, defining filter chains, public/private endpoints, and CORS settings.
-   **`JwtRequestFilter.java`**: A filter that intercepts requests to validate JWT tokens.
-   **`JwtUtils.java`**: Utility class for generating and parsing JWTs.
-   **`WebConfig.java`**: General web configurations.
-   **`DataSeeder.java`**: (If present) Initial data population.

### `controller`
The entry point for API requests. Controllers map HTTP requests to service methods.
-   **`AdminElectionController`**: Handles election administration.
-   **`AdminUserController`**: Handles user administration.
-   **`AuthController`**: Handles login and registration.
-   **`UserController`**: Handles user profile operations.
-   **`UserElectionController`**: Handles voting and election viewing for users.

### `dto` (Data Transfer Objects)
Classes used to transfer data between subsystems (e.g., from Controller to Service) without exposing internal database entities.
-   Examples: `LoginRequest`, `RegisterRequest`, `ElectionCreationRequest`, `UserUpdateDTO`.

### `entity`
JPA Entities representing the database schema.
-   **Core**: `User`, `Admin`, `Election`, `Candidate`, `Vote`.
-   **Verification**: `DummyAadharRecord`, `DummyVoterIdRecord`.
-   **Results**: `ElectionResult`, `ElectionReport`, `VoterElectionStatus`.

### `repository`
Interfaces extending `JpaRepository` for database access. These interfaces provide CRUD operations and custom query methods.
-   Examples: `UserRepository`, `ElectionRepository`, `VoteRepository`.

### `service`
Contains the business logic of the application. Services interact with repositories to perform operations and enforce business rules.
-   **`AuthService`**: Authentication logic.
-   **`ElectionService`**: Election lifecycle management.
-   **`VoteService`**: Voting rules and execution.
-   **`UserService`**: User data management.
-   **`VerificationService`**: Identity verification logic.

## Architecture Layers

1.  **Presentation Layer (Controllers)**: Receives HTTP requests, validates basic input, and calls the appropriate Service. Returns `ResponseEntity` objects.
2.  **Service Layer (Services)**: Implements business logic (e.g., "Has this user already voted?", "Verify Aadhar number"). Transaction management (`@Transactional`) happens here.
3.  **Data Access Layer (Repositories)**: Abstracts the underlying database interactions using Spring Data JPA.
4.  **Database**: MySQL database storing the persistent data.
