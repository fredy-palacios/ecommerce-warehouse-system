# E-Commerce Warehouse System

**Inventory management system with Java 17 and Oracle Database on Docker, applying layered architecture (UI, Service, DAO), clean code principles, professional testing and CI/CD automation.**

<p> 
  <img src="https://github.com/fredy-palacios/imagen-readme/blob/main/ecommerce-warehouse-system/screenshots/demo-project.gif" alt="Clean Code" height="375">
</p>

![Build](https://github.com/fredy-palacios/ecommerce-warehouse-system/actions/workflows/maven.yml/badge.svg)
![Java](https://img.shields.io/badge/-Java%2017-007396?style=flat&logo=openjdk&logoColor=white)
![Oracle XE](https://img.shields.io/badge/-Oracle%2021c%20XE-F80000?style=flat&logo=oracle&logoColor=white)
![JUnit5](https://img.shields.io/badge/-JUnit%205-25A162?style=flat&logo=junit5&logoColor=white)
![Maven](https://img.shields.io/badge/-Maven-C71A36?style=flat&logo=apache-maven&logoColor=white)
![JDBC](https://img.shields.io/badge/-JDBC-007396?style=flat)
![SQL](https://img.shields.io/badge/-SQL-336791?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/-Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Mockito](https://img.shields.io/badge/-Mockito-C5D9C8?style=flat)
![Git](https://img.shields.io/badge/-Git-F05032?style=flat&logo=git&logoColor=white)

Project inspired by my real-world experience in stock control and inventory management using ERP systems in the e-commerce sector.

## ✨ Features

- **Clean Architecture**: Layer separation for easier maintenance and scalability
- **Robust Testing**: Unit tests with JUnit 5 and Mockito
- **Complete Validations**: InputValidator with regex patterns
- **Security**: Password hashing with BCrypt
- **Comprehensive Management**: Products, Categories, Users, and Stock
- **CI/CD Pipeline**: Automated testing with GitHub Actions

## 🛠️ Technologies

- Java 17
- JDBC + Oracle Database 21c XE
- JUnit 5 + Mockito
- Maven
- BCrypt (jbcrypt)
- JColor

## 📂 Project Structure

<p>
  <img src="https://github.com/fredy-palacios/imagen-readme/blob/main/ecommerce-warehouse-system/diagrams/ecommerce-warehouse-system-diagram.png" alt="Clean Code" height="400">
</p>

## 🚀 Installation

### Prerequisites

```bash
java --version  # Java 17+
mvn --version   # Maven 3.8+
docker --version
```

### Quick Start

```bash
# Clone repository
git clone git@github.com:fredy-palacios/ecommerce-warehouse-system.git
cd ecommerce-warehouse-system

# Run Oracle with Docker
docker run -d --name oracle-xe -p 1521:1521 -e ORACLE_PASSWORD=password gvenzl/oracle-xe:21-slim

# Create database schema
docker cp database/schema.sql oracle-xe:/tmp/
docker exec -it oracle-xe sqlplus system/password@localhost:1521/XE @/tmp/schema.sql

# Configure connection
cp src/main/resources/database.properties.example src/main/resources/database.properties

# Compile and run
mvn clean compile
mvn exec:java -Dexec.mainClass="com.fredypalacios.Main"
```

🐳 <b>Useful Docker Commands</b>

```bash
docker start oracle-xe      # Start
docker stop oracle-xe       # Stop
docker logs oracle-xe       # View logs
```

## 🧪 Testing

```bash
mvn test                         # Run all tests
mvn test -Dtest=UserServiceTest  # Run specific test
mvn jacoco:report                # Generate report
```

**107 tests** distributed across:
- CategoryServiceTest
- UserServiceTest
- ProductServiceTest
- InputValidatorTest
- PasswordHasherTest

## 📊 Functionality

### Users
- Complete CRUD operations
- 4 role types (Manager, Picker, Receiver, Controller)
- Password encryption with BCrypt
- Advanced search and filtering

### Products
- Complete CRUD operations
- Real-time stock control
- Low stock alerts and notifications
- Unique SKU validation

### Categories
- Complete CRUD operations
- Enable/disable functionality
- Product associations
- Input validation with regex

## 📄 License

Distributed under MIT License. See `LICENSE` for more information.

## 👤 Author

**Fredy Palacios**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/fredypalacios/)
[![Portfolio](https://img.shields.io/badge/Portfolio-000000?style=for-the-badge&logo=google-chrome&logoColor=white)](https://fredypalacios.com/)
