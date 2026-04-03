# ProjectHub Backend | Spring Boot & MongoDB

The backend of ProjectHub is a high-performance, scalable REST API built with the Spring Boot framework. It manages the core business logic, user authentication, and real-time data persistence for a multi-user project management ecosystem.

---

##  Tech Stack

* **Framework:** Spring Boot 3.x
* **Language:** Java 17+
* **Security:** Spring Security & Stateless JWT (JSON Web Tokens)
* **Database:** MongoDB (NoSQL)
* **API Architecture:** RESTful Design
* **Build Tool:** Maven/Gradle

---

##  Core Features

* **Stateless Authentication:** Secure login and registration using JWT to maintain session state across distributed environments.
* **Role-Based Access Control (RBAC):** Granular authorization levels (e.g., `ROLE_USER`, `ROLE_ADMIN`) to protect sensitive project management operations.
* **Project & Task Lifecycle:** Full CRUD capabilities for managing complex project structures, task assignments, and progress states.
* **Efficient Data Access:** Optimized MongoDB aggregation and indexing for fast retrieval of user activity logs and project statistics.
* **Exception Handling:** Global exception handling for consistent API error responses.

---

##  Security Implementation

The application utilizes a custom **Spring Security Filter Chain** to intercept requests:
1.  **JWT Authentication Filter:** Validates the presence and integrity of the Bearer token in the `Authorization` header.
2.  **UserDetailsService:** Loads user-specific data from MongoDB to populate the `SecurityContext`.
3.  **Password Encoding:** Utilizes `BCryptPasswordEncoder` for secure storage of user credentials.

---

##  Project Structure

```text
src/main/java/com/saurabh/projecthub/
├── config/         # Security and Database configurations
├── controller/     # REST API Endpoints
├── dto/            # Data Transfer Objects for request/response bodies
├── model/          # MongoDB Document Definitions
├── repository/     # Spring Data MongoDB Repositories
├── security/       # JWT Provider and Auth Filters
└── service/        # Business Logic Layer
