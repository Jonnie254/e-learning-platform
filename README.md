# e-learning-platform
his project follows a **microservices architecture** using **Spring Boot**, **Kafka**, **MongoDB**, and other essential tools. The diagram below illustrates the system components and their interactions.
![Screenshot 2025-01-28 174942.png](resources%2FScreenshot%202025-01-28%20174942.png)
## **Components and Flow Explanation**

1. **Frontend (Angular)**
    - The frontend application communicates with the backend through an **API Gateway**.

2. **API Gateway**
    - Acts as a single entry point for clients.
    - Routes requests to appropriate microservices (`Users`, `Courses`, `Enrollment`).

3. **Microservices**
    - **User Service**: Manages user data, stored in **MongoDB**.
    - **Course Service**: Handles course-related operations, uses **PostgreSQL**.
    - **Enrollment Service**: Manages course enrollments, interacts with other services.
    - **Payment Service**: Processes payments and sends **asynchronous confirmations** via **Kafka**.
    - **Notification Service**: Listens to Kafka topics and sends notifications.

4. **Message Broker (Kafka)**
    - Used for asynchronous communication between services.
    - Handles **payment confirmation** and **order confirmation** messages.

5. **Distributed Tracing (Zipkin)**
    - Tracks requests across microservices for better observability.

6. **Service Discovery (Eureka)**
    - Allows services to dynamically discover and communicate with each other.

7. **Configuration Server**
    - Centralized management of configuration properties.

## **Technology Stack**

- **Frontend**: Angular
- **Backend**: Spring Boot Microservices
- **Databases**: MongoDB, PostgreSQL
- **Message Broker**: Apache Kafka
- **Tracing**: Zipkin
- **Service Discovery**: Eureka
- **Configuration Management**: Spring Cloud Config