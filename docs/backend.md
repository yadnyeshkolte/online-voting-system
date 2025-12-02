### Concurrency & Scalability Features

The application is designed to support multiple users accessing it simultaneously, including voting at the same time. Below are the key mechanisms ensuring stability and integrity:

#### 1. Double Voting Prevention
* The database `votes` table has a strict **Unique Constraint** on the combination of `election_id` and `user_id`.
* This guarantees that even if a user sends two requests at the exact same millisecond, the database will reject the second one, ensuring a user can strictly vote only **once per election**.

#### 2. Data Integrity & Transaction Management
* Critical operations like `castVote` are wrapped in `@Transactional`.
* This ensures that all steps (checking eligibility, recording the vote, updating status) happen as a **single atomic unit**.
* If any part fails, the entire transaction rolls back, preventing inconsistent data states.

#### 3. Race Condition Handling (Improved)
* **Issue:** Identified a potential deadlock risk where calculating results for an election could cause conflicts if two votes happened simultaneously.
* **Fix:** Candidates are now locked in a **deterministic order (by ID)** during result calculation.
* This prevents deadlocks and ensures smooth concurrent processing.

#### 4. Cloud Readiness
* The application uses **JWT (JSON Web Token)** for authentication, which is **stateless**. This means the backend does not need to store user session data in memory.
* This makes it highly suitable for **cloud deployment**, as you can easily scale horizontally (add more backend servers) to handle increased traffic without authentication issues.
* The database serves as the single source of truth, handling the concurrency control for data.




-----

  * **Technology Stack:** The backend uses **Spring Data JPA** and **Hibernate**. These technologies automatically use **Parameterized Queries** (Prepared Statements) under the hood. This means user input is treated strictly as data, not as executable code, preventing the database from interpreting input as SQL commands.

  * **Code Verification:**

      * **Standard Queries:** Most database operations rely on built-in repository methods (e.g., `findByEmail`, `save`), which are inherently safe.
      * **Custom Queries:** I examined `UserRepository.java` and found a custom search query:
        ```java
        @Query("SELECT u FROM User u WHERE ... LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) ...")
        List<User> searchUsers(@Param("query") String query);
        ```
        This uses **Named Parameters** (`:query`), which is the correct and secure way to handle dynamic inputs in Spring Boot.
      * **No Raw SQL:** A scan of the codebase confirmed there is no usage of `JdbcTemplate` or manual string concatenation to build SQL queries, which are the common sources of injection vulnerabilities.

-----