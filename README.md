# Spring Boot To-Do API

## Description

This project is a simple REST API for managing tasks (To-Dos), built using Spring Boot. It supports full CRUD
operations and includes features like searching tasks by title and filtering by completion status. The project is
configured to use PostgreSQL for data persistence and includes an in-memory repository for testing purposes.

## Features

- CRUD operations for tasks (To-Dos)
- Search tasks by title
- Filter tasks by completion status
- PostgreSQL database for data persistence
- In-memory repository for development and testing
- Unit testing with an in-memory repository

## Technologies

- Java
- Spring Boot
- Spring Data JPA
- PostgreSQL
- JUnit for testing

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- PostgreSQL

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/springboot-todo-app.git
   ```
2. Navigate to the project directory:
   ```bash
   cd springboot-todo-app
   ```
3. Configure PostgreSQL:
    - Ensure PostgreSQL is running and create a database for the project.
    - You can configure the connection using either the ```application.yml``` or the ```application.properties``` file.  
      <br/>

   Option 1: Using application.yml
   ```yaml
   spring:
    application:
      name: todolist
    datasource:
      driver-class-name: org.postgresql.Driver
      url: jdbc:postgresql://localhost:5432/todolist_db
      username: your-username
      password: your-password
    jpa:
      database-platform: org.hibernate.dialect.PostgreSQLDialect
      show-sql: true
    ```   

   Option 2: Using application.properties
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/todolist_db
     spring.datasource.username=your-username
     spring.datasource.password=your-password
     spring.datasource.driver-class-name=org.postgresql.Driver
     spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
     spring.jpa.show-sql=true
     ```
   
4. Build the project using Maven:
   ```bash
   mvn clean install
   ```

### Running the Application

You can run the application using Maven:

```bash
mvn spring-boot:run
```

The API will be accessible at `http://localhost:8080/todos`.

## API Endpoints

### List all tasks

```bash
GET /todos
```

**Response**: Returns a list of all tasks.

### Get task by ID

```bash
GET /todos/{id}
```

**Response**: Returns the task with the specified ID, or 404 if not found.

### Search tasks by title

```bash
GET /todos/search?title={title}
```

**Response**: Returns a list of tasks matching the provided title. Returns 404 if no tasks are found.

### Filter tasks by completion status

```bash
GET /todos/status?completed={true|false}
```

**Response**: Returns a list of tasks filtered by their completion status (`true` for completed, `false` for
incomplete).

### Create a new task

```bash
POST /todos/create
Content-Type: application/json

{
  "title": "New Task",
  "description": "Task description",
  "completed": false
}
```

**Response**: Creates a new task and returns the created task with status 201.

### Update a task by ID

```bash
PUT /todos/{id}
Content-Type: application/json

{
  "title": "Updated Task",
  "description": "Updated description",
  "completed": true
}
```

**Response**: Updates the task with the given ID and returns the updated task. Returns 404 if the task is not found.

### Delete a task by ID

```bash
DELETE /todos/{id}
```

**Response**: Deletes the task with the specified ID. Returns 204 if successful or 404 if the task is not found.

## In-Memory Repository

For testing purposes, an in-memory repository (`TodoRepositoryInMemory`) is used. This repository stores tasks in a
static list and supports:

- Creating tasks with auto-incremented IDs
- Updating tasks, including their title, description, and completion status
- Searching tasks by title and filtering by completion status
- Deleting tasks by ID

This repository is helpful for testing without needing an external database connection.

## Running Tests

To run unit tests:

```bash
mvn test
```

The tests are designed to work with the in-memory repository.
