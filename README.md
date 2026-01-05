# Bookstore Backend API

A RESTful backend API for managing a bookstore system with multi-entity relationships, order processing, and inventory tracking. Built with Spring Boot and PostgreSQL.

## Features

- **Book Management** - Complete CRUD operations with advanced search and filtering
- **Customer Management** - Customer registration with email validation and profile management
- **Order Processing** - Multi-item orders with automatic inventory deduction and total calculation
- **Inventory Tracking** - Real-time stock updates and low stock alerts
- **Advanced Queries** - Search by category, price range filtering, customer order history
- **Data Validation** - Comprehensive input validation with detailed error responses
- **Service Layer Architecture** - Clean separation of concerns following industry best practices

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 21
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Libraries**: Lombok (boilerplate reduction), Jakarta Validation (input validation)

## Database Schema

### Entities and Relationships

**Book**
- Core information: title, author, ISBN, price, category, stock quantity
- Unique ISBN constraint
- Automatic createdAt/updatedAt timestamps

**Customer**
- Profile data: name, email, phone, address
- Unique email constraint
- One-to-many relationship with Orders

**Order**
- Order header: customer reference, total amount, order date
- One-to-many relationship with OrderItems
- Automatic total calculation from line items

**OrderItem**
- Line items: book reference, quantity, price snapshot
- Many-to-one relationships with Order and Book
- Captures price at time of order (historical accuracy)

### Entity Relationship Diagram
```
Customer (1) ──< (N) Order
                     │
                     └──< (N) OrderItem >── (N) Book
```

**Key Design Decisions:**
- OrderItem captures price at purchase time (prevents historical inconsistencies)
- Cascade operations on Order → OrderItem (delete order removes all items)
- Foreign key constraints prevent orphaned records

## Prerequisites

- Java 21 or higher
- PostgreSQL 12 or higher
- Maven 3.6+
- Postman (recommended for API testing)

## Setup Instructions

### Database Setup

1. **Create PostgreSQL database:**
```sql
CREATE DATABASE bookstore_db;
```

2. **Configure application properties:**

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bookstore_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

3. **Tables auto-create on first run** via Hibernate DDL

### Running the Application

**Build and run:**
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## API Documentation

### Book Endpoints

#### Create Book
```http
POST /api/books
Content-Type: application/json

{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "price": 47.99,
  "category": "Programming",
  "stockQuantity": 25
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "price": 47.99,
  "category": "Programming",
  "stockQuantity": 25,
  "createdAt": "2024-12-20T10:30:00",
  "updatedAt": "2024-12-20T10:30:00"
}
```

**Validation Rules:**
- `title`: Required, 1-200 characters
- `author`: Required, 1-100 characters
- `isbn`: Required, unique, valid ISBN format
- `price`: Required, positive decimal
- `stockQuantity`: Required, non-negative integer
- `category`: Optional, max 50 characters

#### Get All Books
```http
GET /api/books
```

#### Search by Category
```http
GET /api/books/search/category?category=Programming
```

#### Filter by Price Range
```http
GET /api/books/search/price-range?minPrice=20.00&maxPrice=50.00
```

#### Low Stock Alert
```http
GET /api/books/low-stock?threshold=10
```

**Response:**
```json
[
  {
    "id": 3,
    "title": "Design Patterns",
    "stockQuantity": 5,
    ...
  }
]
```

---

### Customer Endpoints

#### Create Customer
```http
POST /api/customers
Content-Type: application/json

{
  "name": "Alice Johnson",
  "email": "alice.johnson@example.com",
  "phone": "514-555-0123",
  "address": "123 Main St, Montreal, QC H3A 1A1"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Alice Johnson",
  "email": "alice.johnson@example.com",
  "phone": "514-555-0123",
  "address": "123 Main St, Montreal, QC H3A 1A1",
  "createdAt": "2024-12-20T10:35:00",
  "updatedAt": "2024-12-20T10:35:00"
}
```

**Validation Rules:**
- `name`: Required, 1-100 characters
- `email`: Required, unique, valid email format
- `phone`: Optional, max 20 characters
- `address`: Optional, max 200 characters

#### Get Customer by ID
```http
GET /api/customers/{id}
```

#### Update Customer
```http
PUT /api/customers/{id}
Content-Type: application/json

{
  "name": "Alice Johnson",
  "email": "alice.j@example.com",
  "phone": "514-555-9999",
  "address": "456 Oak Ave, Montreal, QC"
}
```

---

### Order Endpoints

#### Create Order
```http
POST /api/orders
Content-Type: application/json

{
  "customerId": 1,
  "items": [
    {
      "bookId": 1,
      "quantity": 2
    },
    {
      "bookId": 2,
      "quantity": 1
    }
  ]
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "customer": {
    "id": 1,
    "name": "Alice Johnson",
    "email": "alice.johnson@example.com"
  },
  "items": [
    {
      "id": 1,
      "book": {
        "id": 1,
        "title": "Clean Code",
        "author": "Robert C. Martin"
      },
      "quantity": 2,
      "priceAtPurchase": 47.99
    },
    {
      "id": 2,
      "book": {
        "id": 2,
        "title": "Refactoring",
        "author": "Martin Fowler"
      },
      "quantity": 1,
      "priceAtPurchase": 54.99
    }
  ],
  "totalAmount": 150.97,
  "orderDate": "2024-12-20T11:00:00"
}
```

**Order Processing Logic:**
1. Validates customer exists
2. Validates all books exist
3. Checks sufficient inventory for each book
4. Creates order with all items atomically
5. Deducts inventory for each book
6. Calculates total amount automatically
7. Captures current book prices (historical accuracy)

**Business Rules:**
- Order creation fails if any book has insufficient stock
- Inventory is deducted immediately upon order creation
- Price is captured at time of purchase (prevents historical data issues)
- Transaction is atomic (all or nothing)

#### Get Customer Order History
```http
GET /api/orders/customer/{customerId}
```

**Response:**
```json
[
  {
    "id": 1,
    "totalAmount": 150.97,
    "orderDate": "2024-12-20T11:00:00",
    "items": [...]
  },
  {
    "id": 2,
    "totalAmount": 89.95,
    "orderDate": "2024-12-19T14:30:00",
    "items": [...]
  }
]
```

---

## Error Responses

### Validation Error (400 Bad Request)
```json
{
  "timestamp": "2024-12-20T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "title": "must not be blank",
    "price": "must be greater than 0"
  }
}
```

### Resource Not Found (404 Not Found)
```json
{
  "timestamp": "2024-12-20T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Book not found with id: 999"
}
```

### Business Rule Violation (400 Bad Request)
```json
{
  "timestamp": "2024-12-20T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient stock for book: Clean Code. Available: 2, Requested: 5"
}
```

### Duplicate Email (409 Conflict)
```json
{
  "timestamp": "2024-12-20T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Email already exists: alice@example.com"
}
```

---

## Project Structure

```
src/main/java/com/wooseok/bookstore/
├── controller/              # REST API endpoints
│   ├── BookController.java
│   ├── CustomerController.java
│   └── OrderController.java
├── service/                 # Business logic layer
│   ├── BookService.java
│   ├── BookServiceImpl.java
│   ├── CustomerService.java
│   ├── CustomerServiceImpl.java
│   ├── OrderService.java
│   └── OrderServiceImpl.java
├── repository/              # Data access layer
│   ├── BookRepository.java
│   ├── CustomerRepository.java
│   ├── OrderRepository.java
│   └── OrderItemRepository.java
├── model/                   # JPA entities
│   ├── Book.java
│   ├── Customer.java
│   ├── Order.java
│   └── OrderItem.java
├── dto/                     # Data transfer objects
│   ├── BookDTO.java
│   ├── CustomerDTO.java
│   ├── OrderDTO.java
│   └── OrderItemDTO.java
└── exception/               # Exception handling
    ├── GlobalExceptionHandler.java
    ├── ResourceNotFoundException.java
    └── InsufficientStockException.java
```

## Architecture & Design Patterns

### 3-Tier Layered Architecture
1. **Controller Layer** - Handles HTTP requests, delegates to service layer
2. **Service Layer** - Contains business logic, manages transactions
3. **Repository Layer** - Data access using Spring Data JPA

### Design Patterns Implemented
- **DTO Pattern** - Separates API contracts from database entities
- **Service Layer Pattern** - Encapsulates business logic
- **Repository Pattern** - Abstracts data access
- **Global Exception Handling** - Centralized error responses with `@ControllerAdvice`
- **Dependency Injection** - Spring manages component lifecycle

### Key Technical Implementations
- **Transaction Management** - `@Transactional` on service methods ensures ACID properties
- **Entity Relationships** - Bidirectional mappings with proper cascade and fetch strategies
- **Validation** - Jakarta Bean Validation annotations (`@NotBlank`, `@Email`, `@Positive`)
- **Lombok** - Reduces boilerplate with `@Data`, `@Builder`, `@NoArgsConstructor`
- **Timestamp Auditing** - Automatic `createdAt`/`updatedAt` tracking

## Testing

### Postman Collection
Import `Bookstore_API.postman_collection.json` for comprehensive API testing.

### Manual Test Scenarios
✅ **Book CRUD Operations**
- Create, read, update, delete books
- Search by category and price range
- Low stock alerts

✅ **Customer Management**
- Customer registration with validation
- Email uniqueness enforcement
- Profile updates

✅ **Order Processing**
- Multi-item order creation
- Inventory deduction verification
- Insufficient stock handling
- Customer order history

✅ **Error Handling**
- Validation errors with detailed messages
- Resource not found scenarios
- Business rule violations
- Duplicate entry prevention

## Known Limitations

- No pagination implemented (will be needed for production at scale)
- No order status tracking (all orders are implicitly "completed")
- No soft deletes (deletions are permanent)
- No authentication/authorization (all endpoints are public)

## Future Enhancements

- [ ] JWT authentication and role-based access control
- [ ] Pagination and sorting for list endpoints
- [ ] Order status workflow (PENDING → PROCESSING → SHIPPED → DELIVERED)
- [ ] Shopping cart functionality (temporary order holding)
- [ ] Email notifications for order confirmations
- [ ] Full-text search for books
- [ ] Swagger/OpenAPI documentation
- [ ] Unit and integration tests
- [ ] Docker containerization
- [ ] CI/CD pipeline

## Development Timeline

**Week 4 (Dec 16-21):**
- Mon: Database schema, 4 entities, JPA relationships
- Tue: DTOs, services, Book & Customer endpoints
- Wed: Order processing with inventory management
- Thu: Advanced queries, global exception handling
- Fri: Comprehensive Postman testing, documentation
- Sat: Code polish, final testing

**Status:** ✅ Complete

## Author

**Woo Seok Lee** | [GitHub: @Froderic](https://github.com/Froderic)  
Backend Developer | wooseoklee26@gmail.com  
December 2025

## Technical Highlights

This project demonstrates proficiency in:
- **Spring Boot Framework** - Configuration, auto-configuration, dependency injection
- **Spring Data JPA** - Entity relationships, custom queries, repository pattern
- **RESTful API Design** - Resource naming, HTTP methods, status codes
- **Database Design** - Normalization, foreign keys, constraints
- **Service Layer Architecture** - Transaction management, business logic separation
- **Exception Handling** - Global handlers, custom exceptions, error responses
- **DTO Pattern** - API contract separation from domain models

## License

This project is created for educational purposes as part of a software engineering portfolio.
