# nbooking

A REST API (with a minimal web UI) for managing reservations between businesses and customers — think barbers, restaurants, or hotels booking out resources/slots. Built with Spring Boot, Spring Security (JWT), and Spring Data JPA.

## Features

- **Two account types**: Businesses register/login with email + password; Customers can be guest records (no password) or full accounts with login.
- **JWT authentication** — stateless, role-based (`BUSINESS` / `CUSTOMER`), protecting mutation endpoints while keeping reads and reservation flows open.
- **Resource management** — each business owns a set of bookable resources (e.g. a chair, a table, a room).
- **Reservation engine** — booking a resource checks for time-overlap conflicts before confirming, and reservations can be cancelled.
- **Minimal web UI** — plain HTML/CSS/vanilla JS pages served as static resources, covering the full flow: login/register → browse businesses → browse resources → book/cancel reservations, plus standalone customer management.

## Tech Stack

| Layer | Choice |
|---|---|
| Language / Runtime | Java 21 |
| Framework | Spring Boot 4.0.6 (Web, Data JPA, Validation, Security) |
| Database | MySQL |
| Auth | JWT (`jjwt`), Spring Security filter chain |
| Build | Maven |
| Frontend | Static HTML + vanilla JS (no framework, no build step) |

## Architecture

Standard layered Spring MVC structure under `com.notpatch.nbooking`:

```
controller/   REST endpoints, thin pass-through to services
service/      business logic (overlap checks, password hashing, auth)
repository/   Spring Data JPA repositories
model/        JPA entities: Business, Resource, Customer, Reservation
security/     JWT filter, JwtUtil, SecurityConfig
exception/    global @RestControllerAdvice error mapping
```

**Entity relationships:** a `Business` owns many `Resource`s. A `Reservation` references one `Resource` and one `Customer`, and carries a `status` (`PENDING` / `CONFIRMED` / `CANCELLED`) plus `startTime`/`endTime`.

**Reservation flow:** creating a reservation looks up the resource and customer, rejects time-overlapping bookings on the same resource (ignoring cancelled ones), then confirms and saves.

## API Overview

| Method | Endpoint | Auth |
|---|---|---|
| `POST` | `/api/auth/business/register` | public |
| `POST` | `/api/auth/business/login` | public |
| `POST` | `/api/auth/customer/login` | public |
| `GET` | `/api/businesses` | public |
| `GET` | `/api/businesses/{id}` | public |
| `DELETE` | `/api/businesses/{id}` | BUSINESS |
| `GET` | `/api/businesses/{businessId}/resources` | public |
| `GET` | `/api/businesses/{businessId}/resources/{id}` | public |
| `POST` | `/api/businesses/{businessId}/resources` | BUSINESS |
| `DELETE` | `/api/businesses/{businessId}/resources/{id}` | BUSINESS |
| `GET` | `/api/customers` | public |
| `GET` | `/api/customers/{id}` | public |
| `POST` | `/api/customers` | public (password optional → guest record) |
| `POST` | `/api/reservations?resourceId=&customerId=` | public |
| `GET` | `/api/reservations/resource/{resourceId}` | public |
| `GET` | `/api/reservations/{id}` | public |
| `PATCH` | `/api/reservations/{id}/cancel` | public |

## Running Locally

**Requirements:** Java 21, a local MySQL instance.

1. Create a database and configure credentials in `src/main/resources/application.properties` (defaults to `jdbc:mysql://localhost:3306/nbooking`). Schema is auto-managed via `ddl-auto=update` — no migration tool needed.
2. Run the app:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Open `http://localhost:8080/` in a browser for the web UI, or hit the `/api/...` endpoints directly.

## Running Tests

```bash
./mvnw test
```

## Web UI

The static frontend under `src/main/resources/static/` covers:

- `index.html` — business login/register, customer login
- `businesses.html` — browse businesses, delete your own (if logged in as that business)
- `resources.html?businessId=X` — browse/create/delete a business's resources
- `reservations.html?resourceId=Y` — browse, create, and cancel reservations for a resource
- `customers.html` — public customer registration (guest or full account) and listing

No framework, no build tooling — Spring Boot serves these directly as static resources.

## Project History

This project started as a plain CRUD API and grew in stages, each developed through a structured design → plan → implementation → review workflow:

1. Core CRUD for businesses, resources, customers, and reservations, including reservation overlap-conflict checking.
2. JWT-based authentication and role-based authorization for mutation endpoints.
3. A minimal static web UI exposing the full feature set in a browser.
