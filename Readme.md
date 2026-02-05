# Gym CRM Microservices System

A distributed RESTful application for managing gym operations, trainees, trainers, and training sessions. This project has been migrated from a monolithic architecture to a **Microservices Architecture** using Spring Cloud.

## 🏗 Architecture

The system consists of the following microservices:

1.  **Discovery Server (Eureka):** Service registry for dynamic service discovery.
2.  **Trainer Workload Service:** Handles workload calculations and analytics using **MongoDB** (Embedded).
3.  **Gym Main Service:** The core application handling users, authentication, and trainings using **PostgreSQL**. Communicates with Workload Service via **OpenFeign**.

## 🛠 Tech Stack

* **Java:** 17+
* **Spring Boot:** 3.2.x
* **Spring Cloud:** 2023.x (Eureka, OpenFeign, Circuit Breaker/Resilience4j)
* **Databases:**
    * PostgreSQL (Main Service)
    * MongoDB Embedded (Workload Service)
* **Security:** Spring Security + JWT (Shared Secret Key)
* **Build Tool:** Maven (Multi-module)
* **Testing:** JUnit 5, Mockito, Spring Boot Test

## ⚙️ Prerequisites

Before running the application, ensure you have:

* Java JDK 17 or higher installed.
* Maven installed.
* **PostgreSQL** installed and running locally on port `5432`.
* A PostgreSQL database named `gym_db` created.

*(Note: MongoDB is embedded, so no external installation is required for the Workload Service).*

## 🚀 Getting Started

### 1. Configuration
Ensure your `gym-main-service/src/main/resources/application.yml` has the correct PostgreSQL credentials:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/gym_db
    username: your_postgres_username
    password: your_postgres_password
   ```

### 2. Build the Project
Run the following command from the root directory to build all modules:
```
Bash
mvn clean install
```

### 3. Running the Services (Order Matters!)
   You must start the services in the following order to ensure proper registration:

#### 1. Start Discovery Server: Run DiscoveryServerApplication (Port: 8761) Wait until it fully starts.

#### 2. Start Trainer Workload Service: Run WorkloadApplication (Port: 8081) Wait until it registers with Eureka.

#### 3. Start Gym Main Service: Run GymApplication (Port: 8080)

### 4. Verification
   Open the Eureka Dashboard to verify all services are UP: http://localhost:8761

You should see:

* GYM-MAIN-SERVICE

* TRAINER-WORKLOAD-SERVICE

## 🔌 API Usage
The main entry point is the Gym Main Service on port 8080.

Authentication Flow
The system uses JWT. You must obtain a token first.

#### 1. Login: POST /api/v1/auth/login

```
JSON
{
"username": "trainer.user",
"password": "password123"
}
```
Response: Copy the token.

#### Core Endpoints
#### 2. Create Training (Triggers Workload Update): 
POST /api/v1/trainings 
Header: Authorization: Bearer <your_token>
```
JSON
{
"traineeUsername": "trainee.user",
"trainerUsername": "trainer.user",
"trainingName": "Cardio",
"trainingDate": "2026-06-01",
"trainingDuration": 60
}
```

#### 3. Update Trainee's Trainers: 
PUT /api/v1/trainees/{username}/trainers Header: 
Authorization: Bearer <your_token>
```
JSON
[ "trainer.user" ]
```

## 🧪 Testing
To run unit and integration tests across all modules:
```
Bash
mvn test
```
* Coverage: Includes Unit tests for Services, Mappers, and DTOs, plus Integration tests for Repositories and Controllers using H2 and Embedded Mongo.

## 🛡 Fault Tolerance
* The system implements Circuit Breaker (Resilience4j) on the Gym Main Service. If the Workload Service goes down:

* The Main Service will not crash.

* The transaction will complete locally.

* An error will be logged, and a fallback method will be triggered (e.g., for future retry via Message Queue).