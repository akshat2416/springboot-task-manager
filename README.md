# 🧩 Task Manager

A **Spring Boot–based Task Management System** that allows teams to create projects, assign tasks, and track progress efficiently.  
This backend service demonstrates clean architecture, RESTful API design, and microservice-ready integration with **RabbitMQ** for notifications and **centralized logging**.

---

## 🚀 Overview

**Task Manager** provides APIs for managing:
- 👤 Users — registration and login  
- 📁 Projects — create and manage projects  
- ✅ Tasks — assign, update, and track status  
- 🔔 Notifications — publish task updates asynchronously via RabbitMQ  
- 🧾 Logging — record all activities centrally through a dedicated logging service  

The application was designed with **scalability**, **decoupling**, and **ease of integration** in mind.

---

## 🧱 Architecture

```plaintext
+-------------------+
|  Task Manager API |  <-- Core service
|-------------------|
| UserController    |
| ProjectController |
| TaskController    |
+--------+----------+
         |
         |  (RabbitMQ)
         v
+-------------------+       +-------------------+
| Notification Svc  | <---> | Logging Service   |
+-------------------+       +-------------------+
```

- **Task Manager** publishes messages (task creation, updates) to **RabbitMQ**.
- **Notification Service** consumes and sends user notifications.
- **Logging Service** consumes and stores logs for monitoring and debugging.

---

## ⚙️ Tech Stack

| Layer | Technology |
|-------|-------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.x |
| **ORM / DB** | Spring Data JPA + PostgreSQL |
| **Message Broker** | RabbitMQ |
| **Build Tool** | Maven |
| **Testing** | Postman |
| **Logging** |  Central Logging Service |
| **API Testing** | Postman (`taskmanager.postman_collection.json`) |

---

## 🧠 Key Features

- RESTful CRUD APIs for Users, Projects, and Tasks  
- Asynchronous communication with **RabbitMQ**  
- Centralized logging using a separate microservice  
- Exception handling and standardized response structure  
- Modular design ready for microservice scaling

---

## 🧩 API Endpoints

**Base URL:** `http://localhost:8080/api`

| Module | Method | Endpoint | Description |
|--------|---------|-----------|-------------|
| **Users** | POST | `/users/register` | Create a new user |
|  | GET | `/users` | Get all users |
| **Projects** | POST | `/projects` | Create a new project |
|  | GET | `/projects` | List all projects |
|  | PUT | `/projects/{id}` | Update project |
|  | DELETE | `/projects/{id}` | Delete project |
| **Tasks** | POST | `/tasks` | Create a new task |
|  | GET | `/tasks` | List all tasks |
|  | PUT | `/tasks/{id}/status` | Update task status |
| **Logs** | GET | `/logs` | Retrieve application logs |

> 📬 For quick testing, import the included **`taskmanager.postman_collection.json`** file into Postman.

---

## 🧰 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/task-manager.git
cd task-manager
```

### 2. Configure the Database
Update `application.properties` with your PostgreSQL credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

### 3. Set Up RabbitMQ
Ensure RabbitMQ is running locally:
```bash
rabbitmq-server
```
Default configuration is used:
```
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### 4. Build & Run the Application
```bash
mvn clean install
mvn spring-boot:run
```

---

## 🧪 Testing the APIs

You can test all endpoints using:
- ✅ **Postman Collection:** `taskmanager.postman_collection.json`

Example request (Create Task):
```json
POST /api/tasks
{
  "title": "Design database schema",
  "description": "Set up JPA entities",
  "status": "TO_DO",
  "projectId": 1,
  "assigneeId": 1
}
```
## 📈 Future Improvements

- Add JWT authentication & role-based access control  
- Implement task analytics dashboard  
- Integrate monitoring tools (Prometheus + Grafana)  
- Add Docker and Kubernetes deployment configuration  

---
