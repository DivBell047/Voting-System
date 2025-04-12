# Voting System Using Microservices

## Overview
This project is a distributed voting system designed using a microservices architecture to ensure scalability, fault tolerance, and high availability. Each microservice is responsible for a specific functionality, ensuring modularity and ease of maintenance.

## Microservices Architecture
The system consists of the following microservices:

### 1. **User Service**
- Handles user registration, authentication, and authorization.
- Implements JWT-based authentication and role-based access control (RBAC).
- Provides endpoints for user management, including login, registration, and profile updates.
- Uses PostgreSQL for user data storage.
- **Key Endpoints:**
  - `POST /api/users/register`: Register a new user
  - `POST /api/users/login`: Authenticate user and generate JWT
  - `GET /api/users/profile/{userId}`: Fetch user profile details

### 2. **Candidate Service**
- Manages candidate registration and details.
- Allows admins to add, update, and retrieve candidate information.
- Stores candidate details in PostgreSQL.
- **Key Endpoints:**
  - `POST /api/candidates/add`: Add a new candidate (Admin only)
  - `GET /api/candidates/list`: Retrieve all candidates
  - `GET /api/candidates/{candidateId}`: Fetch specific candidate details

### 3. **Voting Service**
- Handles secure and concurrent vote casting.
- Implements distributed locking with Redis to prevent duplicate voting.
- Stores vote records in PostgreSQL.
- **Key Endpoints:**
  - `POST /api/votes/register`: Register a new vote
    - Validates user and candidate information
    - Ensures a user can vote only once
  - `GET /api/votes/check/{userId}`: Check voting status for a specific user
    - Returns whether the user has voted
    - Provides vote details if already voted
  - `GET /api/votes/count/{candidateId}`: Retrieve total votes for a candidate

### 4. **Result Service**
- Aggregates and computes voting results in real time.
- Uses Redis caching for fast retrieval of election results.
- Provides APIs for fetching real-time voting results.
- **Key Endpoints:**
  - `POST /api/vote-results/update`: Manually trigger vote count update
  - `GET /api/vote-results/rankings`: Retrieve candidate rankings
  - `GET /api/vote-results/candidate/{candidateId}`: Fetch vote count for a specific candidate

### 5. **Notification Service**
- Sends notifications to users upon successful vote casting via controller.
- Supports email/SMS notifications.
- Stores notification logs in PostgreSQL.
- **Key Endpoints:**
  - `POST /api/notifications/send`: Send notification (email/SMS)
  - `GET /api/notifications/user/{userId}`: Retrieve notification history for a user

# TO IMPLEMENT

### 6. **API Gateway**
- Centralized entry point for all client requests.
- Routes requests to the appropriate microservices.
- Implements rate limiting, authentication, and request validation.
- Built using Spring Cloud Gateway.
- **Key Endpoints:**
  - `GET /api/gateway/health`: Check API Gateway status
  - `POST /api/gateway/authenticate`: Validate user authentication

### 7. **Eureka Discovery Service**
- Provides service registration and discovery.
- Enables dynamic scaling by allowing microservices to register and deregister dynamically.
- Ensures load balancing and fault tolerance.
- **Key Endpoints:**
  - `GET /eureka/apps`: Retrieve list of registered microservices
  - `GET /eureka/apps/{serviceId}`: Get details of a specific microservice

## Inter-Service Communication
- **REST APIs**: Used for synchronous communication between certain services (e.g., API Gateway to User Service).
- **Redis**: Used for caching and distributed locking.

## Deployment and Scalability
- **Docker**: All microservices are containerized for easy deployment.
- **Kubernetes**: Manages orchestration, auto-scaling, and service discovery.
- **PostgreSQL Read Replicas**: Ensures database scalability and high availability.
