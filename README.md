
# 🛍️ Backend Service Shop

> A modern, scalable backend system for an e-commerce platform, built with Spring Boot and following best practices in authentication, authorization, and business logic organization.

---

## 📌 Features

- ✅ JWT Authentication & Authorization
- ✅ Role-Based Access Control (RBAC) with `Role` and `Permission`
- ✅ Product & Category Management with status flags (`isNew`, `isHot`, `isActive`)
- ✅ Shopping Cart and Order Management (with OrderStatus enum)
- ✅ Discount Code System (including `maxUses`, `maxUsesPerUser`, `minOrderAmount`, `validity period`)
- ✅ Discount application logic at order-level and product-level
- ✅ Order Invoice PDF generation with Unicode font support
- ✅ Pagination, Filtering, Sorting using `Spring Data JPA Specification`
- ✅ Exception Handling with Global Error Responses
- ✅ DTO-based request/response structure (clean API)
- ✅ Entity Auditing (CreatedAt, UpdatedAt)

---

## 📁 Project Structure

```
backend-service-shop
├── config            # JWT, CORS, and Security Configurations
├── controller        # RESTful API Endpoints
├── dto               # DTOs for Request and Response
├── entity            # JPA Entities (Product, Order, Discount, etc.)
├── exception         # Custom Exceptions and Handlers
├── helper            # Utilities (PDF Generator, Email Sender, etc.)
├── repository        # Spring Data JPA Repositories
├── service           # Service Interfaces
├── service.impl      # Service Implementations
└── util              # Enums and Utility Classes
```

---

## 🧠 Technologies Used

| Technology        | Purpose                         |
|------------------|---------------------------------|
| Spring Boot       | Core Framework                  |
| Spring Data JPA   | ORM & Repository Layer          |
| Spring Security   | Authentication & Authorization |
| JWT               | Token-based Auth                |
| MySQL             | Relational Database             |
| Lombok            | Boilerplate code reduction      |
| iText (Lowagie)   | PDF invoice generation          |
| Java Mail Sender  | Email sending support           |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven
- MySQL (default: `localhost:3306/backend_shop`)
- Postman (for testing APIs)

### Clone the Repository

```bash
git clone https://github.com/hoangdinhdung05/backend-service-shop.git
cd backend-service-shop
```

### Configure `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/backend_shop
    username: root
    password: your_password

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

jwt:
  secret: your_jwt_secret
  expiration: 86400000
```

### Run the Application

```bash
mvn spring-boot:run
```

---

## 📬 API Overview

### 🔐 Auth APIs

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`

### 👤 User Management

- `GET /api/users`
- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`

### 🛒 Cart & Orders

- `POST /api/cart/add`
- `DELETE /api/cart/remove`
- `POST /api/orders` - *Create order*
- `GET /api/orders` - *List orders*
- `PUT /api/orders/{id}/cancel` - *Cancel order*

### 🧾 Discounts

- `POST /api/discounts` - *Admin creates discount*
- `GET /api/discounts/validate?code=SUMMER50` - *User applies discount*
- Auto-handles `maxUses`, `maxUsesPerUser`, time validity, min order amount

### 🖨️ Invoice Generator

- `GET /api/orders/{id}/invoice` → Download PDF invoice with localized fonts

---

## 🛡️ Role-Based Access Control

- `ADMIN`: Full access to all APIs
- `STAFF`: Limited product/order management
- `USER`: Only personal data and shopping actions
- Permissions stored and managed via DB

---

## 📄 Sample Entities

- `User`, `Role`, `Permission`
- `Product`, `Category`, `ProductImage`
- `Cart`, `CartDetail`, `Order`, `OrderDetail`
- `Discount`, `DiscountUsage`

---

## 👨‍🎓 Author

**Hoàng Đình Dũng**  
- Sinh viên năm 2, chuyên ngành Hệ thống Thông tin  
- Trường Đại học Công nghệ Giao thông Vận tải (UTT)  
- 🌐 [GitHub](https://github.com/hoangdinhdung05)

---

## 📚 License

This project is licensed under the MIT License.  
Feel free to clone and use for learning or building your own e-commerce solution.

---

## ⭐ Contributions

Contributions, issues and feature requests are welcome.  
Feel free to fork the repo and submit a pull request!
