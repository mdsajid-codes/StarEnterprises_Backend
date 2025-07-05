# ⚡ Smart Electricity Billing System

A Spring Boot-based full-stack backend application for managing electricity billing for residential users. This system allows importing user and bill data via Excel, generating and viewing PDF bills, handling user and admin authentication with JWT, and securely storing all billing records.

---

## 🚀 Features

- ✅ **User Management**
  - Add, update, and retrieve user information
  - Import users in bulk via Excel
- ✅ **Billing Management**
  - Generate monthly bills
  - View and download PDF bills
  - Import bills via Excel
- ✅ **Authentication**
  - JWT-based login for users and admins
  - Secure token validation and role separation
- ✅ **Admin Panel**
  - Separate login and management for administrators
- ✅ **PDF Generation**
  - PDF bills saved to local storage with metadata
- ✅ **RESTful APIs**
  - Built with Spring Boot, following best practices
- ✅ **CORS Enabled**
  - Supports cross-origin requests from front-end apps

---

## 📦 Technologies Used

| Layer        | Technology                |
|--------------|---------------------------|
| Backend      | Java 21, Spring Boot      |
| Security     | JWT (JSON Web Token)      |
| Database     | MySQL                     |
| ORM          | Spring Data JPA (Hibernate) |
| File Upload  | Apache POI (Excel Import) |
| PDF Handling | iText / OpenPDF           |
| API Design   | RESTful                   |

---

## 📁 Project Structure

com.example.demo
├── controller
│ ├── AuthController.java
│ ├── AdminAuthController.java
│ ├── AdminController.java
│ ├── BillController.java
│ └── UserController.java
├── dto
│ ├── LoginRequest.java
│ ├── LoginResponse.java
│ └── CreateBillPdf.java
├── model
│ ├── Admin.java
│ ├── Bill.java
│ └── User.java
├── repository
│ ├── AdminRepository.java
│ ├── BillRepository.java
│ └── UserRepository.java
├── service
│ ├── AuthService.java
│ ├── AdminAuthService.java
│ ├── BillService.java
│ └── UserService.java
└── DemoApplication.java


---

## 🔐 Authentication Flow

- JWT token is issued upon successful login for both users and admins.
- Token must be included in the `Authorization` header as `Bearer <token>` in protected API requests.
- `/api/auth/login` and `/api/auth/admin/login` are used for logging in.

---

## 🧾 API Endpoints

### 🧑‍💼 User APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/excel/addUser` | Add single user |
| `POST` | `/api/excel/upload` | Import users from Excel |
| `GET`  | `/api/excel/allUser` | Get all users |
| `GET`  | `/api/excel/user/{username}` | Get user by username |
| `PUT`  | `/api/excel/updateSingleUser/{username}` | Update single user |
| `PUT`  | `/api/excel/updatePassword/{username}` | Update password |
| `PUT`  | `/api/excel/updateBulkUser` | Bulk update users |

### 🧾 Bill APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/bill/import` | Import bills from Excel |
| `GET`  | `/api/bill/{username}/{billMonth}` | Get bill by username and month |
| `GET`  | `/api/bill/{username}` | Get all bills for a user |
| `GET`  | `/api/bill/view/{username}/{billMonth}` | View PDF |
| `GET`  | `/api/bill/download/{username}/{billMonth}` | Download PDF |
| `PUT`  | `/api/bill/updateBill/{username}/{billMonth}` | Update bill |

### 🔐 Authentication APIs

| Endpoint | Description |
|----------|-------------|
| `/api/auth/login` | User login |
| `/api/auth/validate` | Validate user token |
| `/api/auth/admin/login` | Admin login |
| `/api/auth/admin/validate` | Validate admin token |

---

## 🛠️ Setup Instructions

👨‍💻 Developer
Sajid Ali
Smart Electricity Billing System — Full-stack backend

### 🔧 Prerequisites

- Java 21
- MySQL Server
- Maven
- Spring Boot compatible IDE (like IntelliJ or VSCode)

### ⚙️ Configuration

Update your `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/billing_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.application.name=billing-system


