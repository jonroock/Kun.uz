# Kun.uz  — News Platform

A full-featured news platform inspired by [Kun.uz](https://kun.uz). Built with Java Spring Boot (backend) and React (frontend).

---

## Tech Stack

**Backend**
- Java, Spring Boot
- Spring Security (JWT)
- Spring Data JPA, Hibernate
- PostgreSQL
- REST API

**Frontend**
- React

---

## Features

- User registration & login (JWT auth)
- Browse and read news articles by category
- Comment on articles
- Admin panel for managing articles and users
- Role-based access control (Admin / User)

---

## Getting Started

### Prerequisites
- Java 17+
- PostgreSQL
- Node.js & npm

### Backend

```bash
git clone https://github.com/jonroock/Kun.uz.git
cd Kun.uz
```

Configure your database in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/kunuz
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Run the application:

```bash
./mvnw spring-boot:run
```

API will be available at `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
npm start
```

App will be available at `http://localhost:3000`

---

## Author

**Qutbiddinov Jonibek** — [github.com/jonroock](https://github.com/jonroock)
