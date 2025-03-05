# Voting-System
A scalable, fault-tolerant voting system leveraging Spring Boot, Kafka, Redis, MySQL, Docker, and Kubernetes. Supports secure registration, voting, real-time result aggregation, and high availability using distributed microservices. Built with event-driven architecture for concurrency handling and eventual consistency.

## Microservices Architecture
The system consists of the following microservices:

### 1. **User Service**
- Handles user registration, authentication, and authorization.
- Implements JWT-based authentication and role-based access control (RBAC).
- Provides endpoints for user management, including login, registration, and profile updates.
- Uses PostgreSQL for user data storage.

### 2. **Candidate Service**
- Manages candidate registration and details.
- Allows admins to add, update, and retrieve candidate information.
- Stores candidate details in PostgreSQL.

### 3. **Voting Service**
- Handles secure and concurrent vote casting.
- Implements distributed locking with Redis to prevent duplicate voting.
- Uses Kafka for asynchronous vote processing to ensure eventual consistency.
- Stores vote records in PostgreSQL.

### 4. **Result Service**
- Aggregates and computes voting results in real time.
- Listens to Kafka topics for new votes and updates results accordingly.
- Uses Redis caching for fast retrieval of election results.
- Provides APIs for fetching real-time voting results.

### 5. **Notification Service**
- Sends notifications to users upon successful vote casting.
- Supports email/SMS notifications.
- Uses Kafka for event-driven notification processing.
- Stores notification logs in PostgreSQL.

# TO IMPLEMENT
### 6. **API Gateway**
- Centralized entry point for all client requests.
- Routes requests to the appropriate microservices.
- Implements rate limiting, authentication, and request validation.
- Built using Spring Cloud Gateway.

### 7. **Eureka Discovery Service**
- Provides service registration and discovery.
- Enables dynamic scaling by allowing microservices to register and deregister dynamically.
- Ensures load balancing and fault tolerance.

## Inter-Service Communication
- **Kafka**: Used for asynchronous communication between microservices (e.g., vote events, notification events, result updates).
- **REST APIs**: Used for synchronous communication between certain services (e.g., API Gateway to User Service).
- **Redis**: Used for caching and distributed locking.

## Deployment and Scalability
- **Docker**: All microservices are containerized for easy deployment.
- **Kubernetes**: Manages orchestration, auto-scaling, and service discovery.
- **PostgreSQL Read Replicas**: Ensures database scalability and high availability.

