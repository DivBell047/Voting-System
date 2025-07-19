# Voting System Using Microservices

## Overview
This project is a distributed voting system designed using a microservices architecture to ensure scalability, fault tolerance, and high availability. Each microservice is responsible for a specific functionality, ensuring modularity and ease of maintenance. The system is built with Spring Boot, Spring Cloud, and monitored using Prometheus and Grafana.

## Microservices Architecture
The system consists of the following microservices:

### 1. **User Service**
- Handles user registration and data management.
- Can be extended to support JWT-based authentication and role-based access control (RBAC).
- Provides endpoints for user management.
- Uses **MySQL** for user data storage.
- **Key Endpoints:**
  - `POST /user/register`: Register a new user
  - `GET /user/{id}`: Check for a user's existence by ID (for inter-service validation)
  - `GET /user/username/{username}`: Fetch user details by username

### 2. **Candidate Service**
- Manages candidate registration and details.
- Allows for the addition, update, and retrieval of candidate information.
- Stores candidate details in **MySQL**.
- **Key Endpoints:**
  - `POST /candidates/add`: Add a new candidate
  - `GET /candidates/list`: Retrieve all candidates
  - `GET /candidates/{candidateId}`: Fetch specific candidate details

### 3. **Voting Service (Vote Registration)**
- Handles secure and concurrent vote casting.
- Uses a database **unique constraint** to prevent duplicate voting by a single user.
- Stores vote records in **MySQL**.
- **Key Endpoints:**
  - `POST /registration/register`: Register a new vote
    - Validates user and candidate information via API calls
    - Ensures a user can vote only once
  - `GET /registration/check/{userId}`: Check voting status for a specific user
  - `GET /registration/count`: Provides raw vote counts for the Result Service

### 4. **Result Service (Vote Counting)**
- Aggregates and computes voting results.
- Is stateless and fetches data from the Voting Service in real-time.
- Provides APIs for fetching current voting results.
- **Key Endpoints:**
  - `GET /counting/rankings`: Retrieve candidate rankings
  - `GET /counting/candidate/{candidateId}`: Fetch the current vote count for a specific candidate

### 5. **Notification Service**
- Sends notifications to users upon successful vote casting.
- Supports **email notifications** using **Spring Boot Mail (JavaMailSender)**.
- Can be configured to store notification logs in **MySQL**.
- **Key Endpoints:**
  - `POST /notifications/send`: Send an email notification

### 6. **API Gateway**
- Centralized entry point for all client requests.
- Routes requests to the appropriate microservices.
- Can be configured for rate limiting, authentication, and request validation.
- Built using **Spring Cloud Gateway**.

### 7. **Eureka Discovery Service**
- Provides service registration and discovery.
- Enables dynamic scaling by allowing microservices to register and deregister dynamically.
- Ensures load balancing and fault tolerance for inter-service communication.

## Monitoring
- **Prometheus**: Scrapes metrics exposed by each microservice's Spring Boot Actuator endpoint.
- **Grafana**: Visualizes metrics from Prometheus, providing dashboards for system health, performance, and request monitoring.
- **Swagger API**: Each service includes Swagger (OpenAPI) for live API documentation and testing.

## Inter-Service Communication
- **REST APIs**: Used for synchronous communication between services, facilitated by `RestTemplate`. All communication is routed through the API Gateway.

## Deployment and Scalability
- **Docker**: All microservices are containerized for easy and consistent deployment.
- **Docker Compose**: Manages orchestration of the entire application stack for local development and testing, including all services, the database, and the monitoring tools.
