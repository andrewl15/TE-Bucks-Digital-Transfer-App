# TEBucks - Module 2 Capstone

## Overview

TEBucks is a simplified peer-to-peer payment service that enables users to send and request "TE Bucks"—a fictional currency—between accounts. This Java-based application is the backend system for an existing frontend hosted at [https://tebucks.netlify.app](https://tebucks.netlify.app). This capstone project demonstrates core concepts of RESTful API design, Spring Boot, JDBC, PostgreSQL, and secure authentication.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Database Setup](#database-setup)
- [Testing](#testing)
- [TEARS Integration](#tears-integration)

---

## Features

- 🔐 User Registration and Login (JWT-based Authentication)
- 💰 View current balance
- 📤 Send TE Bucks to other users
- 📥 Request TE Bucks from other users
- 📄 View all sent and received transfers
- 🔎 View details of individual transfers
- ⏳ View and manage pending transfer requests
- 🚫 Business logic enforcement:
  - No overdrafts
  - No self-transfers or self-requests
  - No zero or negative amounts
- 🧾 Integration with TEARS (Tech Elevator Aberrant Revenue Service) for logging large or invalid transactions

---

## Tech Stack

- **Java** (Spring Boot, Spring Security)
- **PostgreSQL** for persistent data storage
- **JdbcTemplate** for data access
- **JUnit** for integration testing
- **Postman** for manual API testing
- **JWT** for stateless user authentication

---

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL
- Maven
- Postman (for manual testing)

### Clone and Run

```bash
git clone https://github.com/yourusername/tebucks.git
cd tebucks
