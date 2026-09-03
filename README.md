# NexusOps

## AI-Powered B2B Procurement & Operations Platform

NexusOps is a production-style backend application designed to help organizations manage their procurement and operational workflows from a single platform.

The application supports supplier management, product catalogs, purchase requests, purchase orders, approval workflows, inventory, warehouses, invoicing, notifications, reporting, auditing, and future AI-powered operational assistance.

NexusOps is being built as a **modular monolith**. It is deployed as a single Spring Boot application while maintaining clear boundaries between business domains.

---

## Project Goals

- Build scalable REST APIs using Java 21 and Spring Boot
- Design a clean modular monolith architecture
- Work with MySQL, Spring Data JPA and Hibernate
- Implement transactional business workflows
- Write unit and integration tests
- Implement authentication and authorization
- Handle concurrency and idempotency
- Apply caching and database performance techniques
- Containerize and deploy the application
- Add asynchronous event-driven processing
- Integrate practical AI capabilities using Spring AI

---

## Architecture

```text
Client
  |
  v
Spring Boot Application
  |
  +-- Identity
  +-- Organization
  +-- Supplier
  +-- Product
  +-- Procurement
  +-- Approval
  +-- Inventory
  +-- Warehouse
  +-- Invoice
  +-- Notification
  +-- Reporting
  +-- Audit
  +-- AI
  |
  v
MySQL
```

The application is one deployable unit, but business modules have clear boundaries.

---

## Technology Stack

### Current

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- JUnit 5
- Mockito
- Git
- Logging

### Planned

- Spring Security
- JWT
- OAuth2
- Flyway
- Docker
- Redis
- Testcontainers
- Spring Boot Actuator
- OpenAPI / Swagger
- Cloud Deployment
- Apache Kafka
- Spring AI
- RAG

---

## Core Modules

### Identity & Access
- User management
- Authentication
- Roles and permissions
- JWT
- Refresh tokens
- OAuth2
- Account controls

### Organization
- Organization onboarding
- Departments
- User membership
- Tenant-aware access

### Supplier
- Supplier onboarding
- Contacts
- Categories
- Tax/billing details
- Performance history
- Risk flags

### Product Catalog
- Products and SKUs
- Categories
- Units
- Supplier mapping
- Pricing and taxes
- Reorder thresholds
- Search/filtering/pagination

### Procurement
- Purchase requests
- Purchase orders
- Line items
- Supplier selection
- Status transitions
- Order history

### Approval
- Rule-based approvals
- Approval thresholds
- Sequential approval
- Rejection comments
- Escalation
- Immutable history

### Inventory & Warehouse
- Stock receipts
- Reservations/releases
- Adjustments
- Transfers
- Stock movement history
- Low-stock alerts
- Reconciliation

### Invoicing
- Supplier invoices
- PO matching
- Discrepancy detection
- Due dates
- Payment status
- Audit trail

### Reporting
- Spend by supplier/category
- Inventory health
- Purchase cycle time
- Supplier delivery quality
- Invoice ageing
- Operational KPIs

---

## Project Structure

See `NexusOps_Structure.pdf` for the complete package/class structure.

```text
src/main/java/com/hetpatel/nexusops
├── common
├── identity
├── organization
├── supplier
├── product
├── procurement
├── approval
├── inventory
├── warehouse
├── invoice
├── notification
├── audit
├── reporting
├── ai
└── infrastructure
```

---

## Development Roadmap

1. Foundation
2. Organization and Identity
3. Supplier and Product Catalog
4. Procurement
5. Security + JWT + OAuth2
6. Approval Engine
7. Inventory and Warehouse
8. Transactions + concurrency + idempotency
9. Invoicing + audit
10. Flyway + indexing + OpenAPI
11. Docker + Redis + Testcontainers
12. Observability + CI/CD + cloud
13. Kafka
14. Spring AI + controlled tools + RAG

See `NexusOps_Build_Roadmap.pdf` for the complete roadmap and `NexusOps_Development_Order.pdf` for the recommended learning/build sequence.

---

## Testing

The project uses:

- JUnit 5
- Mockito
- Spring Boot testing
- Testcontainers (planned)

Important test areas:

- Business rules
- Repository queries
- Transactions
- Authentication
- Authorization
- Cross-tenant access
- Inventory concurrency
- Critical REST APIs

---

## Database

MySQL is the primary database.

Schema changes will be version-controlled with Flyway migrations.

```text
V1__create_organizations.sql
V2__create_users.sql
V3__create_suppliers.sql
V4__create_products.sql
V5__create_purchase_requests.sql
V6__create_purchase_orders.sql
...
```

---

## Security

Security will be introduced progressively:

```text
Spring Security
    ↓
Authentication
    ↓
Roles
    ↓
Permissions
    ↓
JWT
    ↓
Refresh Tokens
    ↓
OAuth2
    ↓
Tenant Authorization
```

---

## AI Procurement Copilot

The final AI layer will provide controlled, business-focused assistance.

Example:

> Which suppliers had the highest rejection rate this quarter?

The AI will use controlled backend tools rather than direct database access.

```text
User
 ↓
Spring AI
 ↓
Controlled Backend Tool
 ↓
Business Service
 ↓
Database
 ↓
Structured Result
 ↓
AI Response + Evidence
```

RAG will later be used for procurement policies, contracts and internal documentation.

---

## Engineering Principles

- Prefer business-focused modules over technical package dumping
- Keep controllers thin
- Put business rules in services/domain components
- Use DTOs for API contracts
- Keep repositories behind module boundaries
- Define transaction boundaries explicitly
- Validate input
- Never expose tenant data across organizations
- Avoid unbounded queries
- Use database constraints and indexes deliberately
- Add caching only where it solves a real problem
- Add Kafka/AI only when there is a clear engineering or business reason
- Keep the application deployable as one monolith

---

## Current Development Status

NexusOps is under active development.

The project will evolve alongside my Java backend learning path. New technologies will be introduced when they solve an actual engineering problem rather than being added only for the sake of using more technologies.

---

## Author

Het Patel

B.Tech Computer Science and Engineering
