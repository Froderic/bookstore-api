# Bookstore Backend API

A RESTful backend API for managing a bookstore system with customer orders and inventory tracking. Built with Spring Boot and PostgreSQL.

## Features

- **Book Management**: Complete CRUD operations with search and filtering capabilities
- **Customer Management**: Customer registration and profile management
- **Order Processing**: Multi-item order creation with automatic inventory tracking
- **Inventory Management**: Real-time stock updates and low stock alerts
- **Advanced Queries**: Search by category, price range filtering, customer order history

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 21
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Additional Libraries**: Lombok, Jakarta Validation

## Database Schema

### Entities and Relationships

**Book**
- Core book information (title, author, ISBN, price, stock)
- Category classification
- Automatic timestamps

**Customer**
- Customer profile (name, email, phone, address)
- Unique email constraint
- One-to-many relationship with Orders

**Order**
- Order header (customer, total amount, status, date)
- One-to-many relationship with OrderItems
- Automatic total calculation

**OrderItem**
- Line items for each order
- Many-to-one relationships with Order and Book
- Captures price at time of order

### Entity Relationships
```
Customer (1) ─── (N) Order
Order (1) ─── (N) OrderItem
Book (1) ─── (N) OrderItem
```

## API Endpoints

### Books (`/api/books`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books` | Get all books |
| GET | `/api/books/{id}` | Get book by ID |
| POST | `/api/books` | Create new book |
| PUT | `/api/books/{id}` | Update book |
| DELETE | `/api/books/{id}` | Delete book |
| GET | `/api/books/search/category?category={category}` | Search by category |
| GET | `/api/books/search/price-range?minPrice={min}&maxPrice={max}` | Filter by price |
| GET | `/api/books/low-stock?threshold={number}` | Get low stock items |

### Customers (`/api/customers`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/customers` | Get all customers |
| GET | `/api/customers/{id}` | Get customer by ID |
| POST | `/api/customers` | Create new customer |
| PUT | `/api/customers/{id}` | Update customer |
| DELETE | `/api/customers/{id}` | Delete customer |

### Orders (`/api/orders`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| POST | `/api/orders` | Create new order |
| GET | `/api/orders/customer/{customerId}` | Get customer's orders |

## Setup Instructions

### Prerequisites
- Java 21 or higher
- PostgreSQL 12 or higher
- Maven 3.6+

### Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE bookstore_db;
```

2. Update `application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bookstore_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Running the Application

1. Clone the repository
2. Navigate to project directory
3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## Testing with Postman

A Postman collection is included (`Bookstore_API.postman_collection.json`) for easy API testing.

**To import:**
1. Open Postman
2. Click "Import"
3. Select `Bookstore_API.postman_collection.json`
4. The collection includes pre-configured requests for all endpoints

## Key Implementation Details

### Architecture
- **3-Tier Architecture**: Controller → Service → Repository
- **DTO Pattern**: Separation between entities and API contracts
- **Global Exception Handling**: Centralized error responses with `@ControllerAdvice`

### Business Logic
- **Inventory Management**: Stock quantities automatically decrease when orders are created
- **Order Validation**: Validates sufficient inventory before order creation
- **Price Capture**: OrderItems store price at time of purchase (prevents historical data issues)
- **Constraint Protection**: Foreign key constraints prevent deletion of referenced entities

### Data Validation
- Input validation using Jakarta Bean Validation
- Custom exceptions for business rule violations
- Unique constraints on ISBN and customer email

## Project Structure
```
src/main/java/com/wooseok/bookstore/
├── controller/       # REST endpoints
├── service/          # Business logic
├── repository/       # Data access layer
├── model/            # JPA entities
├── dto/              # Data transfer objects
└── exception/        # Custom exceptions and handlers
```

## Future Enhancements
- Authentication and authorization (Spring Security + JWT)
- Pagination for large datasets
- Advanced search with full-text search
- Order status tracking and updates
- Email notifications for order confirmations
- API documentation with Swagger/OpenAPI

## Author
Woo Seok - Backend Developer in Training

## License
This project is created for educational and portfolio purposes.