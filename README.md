# ShopCraft: Backend Engineering with Spring Boot & Kotlin

> **Master Course**: Build a modern, production-grade E-Commerce backend API from scratch with Spring Boot 4.1.x, Kotlin 2.3.x, and JDK 21 LTS using Test-Driven Development (TDD) and branch-based progression.

---

## 🧭 Course Overview

Welcome to the **Spring Boot with Kotlin Master Course**! This course is designed to guide software engineers from initial project setup to building secure, scalable, and fully tested backend services.

Through a realistic business domain—**ShopCraft** (a high-performance e-commerce catalog, auditing, and authentication engine)—you will master Kotlin idioms, idiomatic Spring patterns, REST architecture, custom validation, Aspect-Oriented Programming, comprehensive testing pyramids, stateless JWT authentication, and OAuth2 social login.

---

## 🛠️ Technology Stack

| Layer / Concern | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Kotlin | `2.3.21` | Modern, null-safe JVM language with concise syntax |
| **Framework** | Spring Boot | `4.1.1` | Modern enterprise framework with reactive & servlet foundations |
| **Runtime** | OpenJDK | `21 LTS` | Modern Java Virtual Machine runtime with Virtual Threads |
| **Build Tool** | Gradle Kotlin DSL | `9.7.1` | Type-safe, declarative project build automation |
| **Persistence** | Spring Data JPA / Hibernate | Bundled | Object-relational mapping, custom specifications, pagination |
| **Databases** | PostgreSQL & H2 | `42.7.x` / In-Memory | Production runtime persistence and fast in-memory execution |
| **AOP** | Spring AspectJ | `4.1.1` | Cross-cutting profiling and transaction-isolated audit logging |
| **Security** | Spring Security & JJWT | `7.x` / `0.12.7` | Stateless JWT filter chains, RBAC, and OAuth2 client flows |
| **Testing** | JUnit 5, Mockito-Kotlin, Testcontainers | `6.4.0` / `2.0.5` | Unit testing, slice testing, and isolated containerized tests |

---

## 📚 Curriculum & Module Roadmap

```
Day 01: Spring Boot with Kotlin ──► Day 02: REST Principles & Dynamic Queries
                                                     │
                                                     ▼
Day 04: AOP & Audit Logging     ◄── Day 03: Validation & RFC 7807 Errors
         │
         ▼
Day 05: Testing Pyramid & Testcontainers
         │
         ▼
Day 06: JWT Authentication & RBAC ──► Day 07: OAuth2 & Social Login Bridge
```

### Module 1: Spring Boot with Kotlin
- Configuring Gradle Kotlin DSL (`build.gradle.kts`) with JDK 21 LTS toolchain.
- Writing controllers, services, and repositories in Kotlin (constructor injection with immutable `val` properties).
- Data classes for DTOs vs. JPA entities (why JPA entities must avoid `data class`, handling `allopen`/`noarg` compiler plugins).
- Kotlin idioms with Spring: null-safety, eliminating `lateinit var`, leveraging Spring Data Kotlin extensions such as `findByIdOrNull()`.

### Module 2: REST Principles
- REST constraints: statelessness, uniform interface, client-server separation.
- HTTP methods and status code semantics:
  - `GET /api/v1/products/{id}` ➔ `200 OK` (safe, idempotent retrieval).
  - `POST /api/v1/products` ➔ `201 Created` with standard RFC `Location` header.
  - `PUT /api/v1/products/{id}` ➔ `200 OK` (idempotent complete replacement).
  - `PATCH /api/v1/products/{id}` ➔ `200 OK` (selective partial attribute modification).
  - `DELETE /api/v1/products/{id}` ➔ `204 No Content` (idempotent resource removal).
- Resource URI conventions: plural nouns, resource hierarchies, avoiding verbs in URIs.
- Pagination, sorting, and dynamic filtering:
  - Spring Data `Pageable` and `Sort`.
  - Dynamic queries using `JpaSpecificationExecutor` with custom JPA Specifications.
  - Standardized generic `PagedResponse<T>` envelope DTO.

### Module 3: Validation & Error Handling
- Jakarta Bean Validation constraints (`@NotNull`, `@NotBlank`, `@Size`, `@Positive`, `@Valid`).
- Kotlin Use-Site Targets: Deep dive into why constructor properties require `@field:` targets (e.g., `@field:NotBlank`) so validation inspects JVM fields during deserialization.
- Custom Validation Constraints: Defining `@ValidSku` annotation (`@Target`, `@Retention`, `@Constraint`) and implementing `ConstraintValidator<ValidSku, String>`.
- Global Exception Handling with `@RestControllerAdvice` and `@ExceptionHandler`.
- Standard RFC 7807 `ProblemDetail` with custom fields: `timestamp` and a structured `errors` array mapping field names, rejected values, and user-friendly messages.
- Handling `MethodArgumentNotValidException`, `HttpMessageNotReadableException`, domain exceptions (`ResourceNotFoundException`, `DuplicateResourceException`), and 500 fallbacks.

### Module 4: Aspect-Oriented Programming (AOP)
- Core AOP principles: Join Points, Pointcuts, and Advice types (`@Around`, `@Before`, `@AfterReturning`, `@AfterThrowing`).
- Execution performance profiling aspect: `@TrackExecutionTime(thresholdMs = 250)` logging execution duration with warning thresholds.
- Audit logging aspect: `@AuditLog(action = "PRODUCT_CREATED")` capturing mutating operations.
- Transactional Isolation: Persisting audit records via a dedicated `AuditService` using `@Transactional(propagation = Propagation.REQUIRES_NEW)` so audit logs survive business transaction rollbacks.
- Admin auditing endpoint: `GET /api/v1/admin/audit-logs`.

### Module 5: Unit & Integration Testing
- The Spring Boot Testing Pyramid: balancing unit tests, slice tests, and full integration tests.
- Unit testing: Isolating service logic with JUnit 5 Jupiter and `mockito-kotlin` (`whenever`, `verify`, `any`).
- Web slice testing: `@WebMvcTest(ProductController::class)` with `MockMvc`, asserting status codes, headers (`Location`), and JSON payloads with `jsonPath`.
- Data slice testing: `@DataJpaTest` testing entity persistence, queries, and dynamic specifications.
- Testcontainers integration testing: `@SpringBootTest` with PostgreSQL Testcontainers using `@ServiceConnection` and the singleton container pattern for reproducible, mock-free integration testing.

### Module 6: JWT Authentication & RBAC
- JWT structure: Header (algorithm & type), Payload (claims, issuer, subject, expiration, roles), and Signature (HMAC-SHA256).
- Stateless security vs session-based authentication in modern distributed architectures.
- BCrypt password hashing (`PasswordEncoder`).
- Dual roles: `ROLE_USER` vs. `ROLE_ADMIN` with Role-Based Access Control (RBAC).
- Authentication endpoints: `/api/v1/auth/register`, `/api/v1/auth/login`, and `/api/v1/auth/me`.
- Generating and validating tokens using modern JJWT (`0.12.7`).
- Security filter chain: `OncePerRequestFilter` Bearer token extraction, stateless session policy (`SessionCreationPolicy.STATELESS`), and custom `AuthenticationEntryPoint` returning RFC 7807 401 envelopes.

### Module 7: OAuth2 Overview & Social Login
- OAuth2 roles and core concepts: Client, Resource Owner, Authorization Server, Resource Server.
- The Authorization Code Grant flow: authorization request, user consent, code exchange, and token acquisition.
- Spring Security OAuth2 Client integration (`spring-boot-starter-security-oauth2-client`).
- GitHub OAuth2 app integration.
- Custom `OAuth2UserService` to load provider attributes and auto-provision / synchronize user entities in PostgreSQL.
- The **OAuth2-to-JWT Bridge pattern**: An `AuthenticationSuccessHandler` that intercepts successful OAuth logins, mints an internal stateless JWT token, and redirects the client with the Bearer token.

---

## 🌳 Branch Architecture & Navigation

The course repository is structured into **15 branches**: `main` + 7 pairs of starter and solution branches.

```
main (Roadmap, Curriculum Overview, Environment Setup Guide)
 │
 ├── day-01-spring-boot-kotlin-starter  /  day-01-spring-boot-kotlin-solution
 ├── day-02-rest-principles-starter     /  day-02-rest-principles-solution
 ├── day-03-validation-error-handling-starter / day-03-validation-error-handling-solution
 ├── day-04-aop-starter                 /  day-04-aop-solution
 ├── day-05-testing-starter             /  day-05-testing-solution
 ├── day-06-jwt-authentication-starter  /  day-06-jwt-authentication-solution
 └── day-07-oauth2-overview-starter     /  day-07-oauth2-overview-solution
```

### Branch Navigation Table

| Day / Module | Starter Branch | Solution Branch | Focus Area |
| :--- | :--- | :--- | :--- |
| **Day 01** | `git checkout day-01-spring-boot-kotlin-starter` | `git checkout day-01-spring-boot-kotlin-solution` | Kotlin setup, entities, DTOs, repository |
| **Day 02** | `git checkout day-02-rest-principles-starter` | `git checkout day-02-rest-principles-solution` | REST endpoints, specifications, pagination |
| **Day 03** | `git checkout day-03-validation-error-handling-starter` | `git checkout day-03-validation-error-handling-solution` | Bean validation, `@field:`, ProblemDetail |
| **Day 04** | `git checkout day-04-aop-starter` | `git checkout day-04-aop-solution` | AOP profiling, audit logging, REQUIRES_NEW |
| **Day 05** | `git checkout day-05-testing-starter` | `git checkout day-05-testing-solution` | Mockito-Kotlin, WebMvcTest, Testcontainers |
| **Day 06** | `git checkout day-06-jwt-authentication-starter` | `git checkout day-06-jwt-authentication-solution` | JJWT 0.12.7, SecurityFilterChain, RBAC |
| **Day 07** | `git checkout day-07-oauth2-overview-starter` | `git checkout day-07-oauth2-overview-solution` | OAuth2 social login, JWT bridge handler |

---

## 🎯 Pedagogical Artifacts on Every Branch

Every branch in this course includes 4 first-class pedagogical artifacts located in the project root:

1. **`LESSON.md`**: Complete, self-contained textbook chapter containing:
   - Theoretical explanations and architectural concepts.
   - Kotlin language mechanics and Spring architectural trade-offs.
   - Interactive Mermaid sequence and class diagrams.
   - Step-by-step implementation walkthrough corresponding to `TODO("Step X - ...")`.
   - Automated self-verification guide.
2. **`requests/{module}.http`**: Executable HTTP requests with environment variables and sample payloads for IntelliJ IDEA / REST Client.
3. **`requests/{module}.curl.sh`**: Executable bash script demonstrating all endpoints and error cases with colored terminal output.
4. **`requests/{module}.postman_collection.json`**: Pre-configured Postman collection with test assertions and automatic token extraction.

---

## 🚦 Learning Workflow: Red-Green-Refactor

1. **Checkout the Starter Branch**:
   ```bash
   git checkout day-01-spring-boot-kotlin-starter
   ```
2. **Run the Pre-Written Tests**:
   ```bash
   ./gradlew test
   ```
   *Notice the tests fail cleanly with `NotImplementedError` or assertion errors.*
3. **Read `LESSON.md` & Follow the TODOs**:
   - Open `LESSON.md` to understand the theory and requirements.
   - Implement each `TODO("Step X - ...")` in the codebase.
4. **Achieve All-Green Tests**:
   - Run `./gradlew test` until all tests pass 100% green.
5. **Compare with the Reference Solution**:
   ```bash
   git diff day-01-spring-boot-kotlin-solution
   ```

---

## ⚡ Environment Setup & Getting Started

### 1. Prerequisites
- **JDK 21 LTS**: Ensure Java 21 is installed and available on your PATH:
  ```bash
  java -version
  ```
- **Docker**: Required for running PostgreSQL Testcontainers in Module 5.
- **IDE**: [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Ultimate or Community) with Kotlin plugin enabled.

### 2. Verify Scaffolding & Run Tests
Clone the repository and run the test suite:
```bash
./gradlew test
```

### 3. Run the Application
Launch the ShopCraft application locally:
```bash
./gradlew bootRun
```
Access the H2 database console in your browser at:
`http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:shopcraft`, Username: `sa`, Password: *(empty)*).
