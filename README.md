
# ScrollSystem Full-Stack Application

## Overview

ScrollSystem is a full-stack application with a **Spring Boot** backend and a **React** frontend. The backend uses **MySQL** as the database and is containerized with **Docker**, while the frontend is built using modern JavaScript technologies and connected to the backend via **Axios**. The project is organized with an **MVC** architecture for the backend and a modular structure for the frontend.

### Backend Highlights:
- **Spring Boot** for backend services.
- **MySQL** database with Docker-based containerization.
- **JWT** authentication and role-based authorization.
- **RESTful API** endpoints for user management.
- **ORM (Object-Relational Mapping)** with Spring Data JPA to interact with the database, ensuring easier querying and data manipulation.


### Frontend Technologies Used:
- **ReactJS** JavaScript library for building user interface.
- **Redux** for state management.
- **TailwindCSS** Utility-first CSS framework
- **Axios** for API calls to the backend.


## Project Structure
### Project Directory
```
├── backend/                             # Directory for running backend for the project
├── frontend/                            # Directory for running frontend for the project
├── .gitignore                           # File for ignore some specific file such as .env in both backend and frontend, folder build, node_modules,..
└── makefile                             # simplify the process of running the project
```

### Backend Directory (`backend/`)

```
backend/
├── src/
│   ├── main/
│   │   ├── java/org/scrollSystem/
│   │   │   ├── config/                  # Configuration files (Security, CORS)
│   │   │   ├── controller/              # RESTful controllers
│   │   │   ├── exception/               # Custom exceptions
│   │   │   ├── models/                  # JPA Entities (User, FileStorage)
│   │   │   ├── repository/              # Repositories for database interaction
│   │   │   ├── request/                 # DTOs for requests
│   │   │   ├── response/                # DTOs for responses
│   │   │   └── service/                 # Business logic services
│   └── resources/
│       └── application.properties       # Spring Boot configuration
├── Dockerfile                           # Docker configuration for backend
├── docker-compose.yml                   # Docker Compose file for backend services
├── build.gradle                         # Gradle configuration file
└── .env.example                         # Environment variable example
```

### Frontend Directory (`frontend/`)

```
frontend/
├── public/                               # Static files (index.html)
├── src/                                  # Frontend source code
│   ├── assets/                           # Project assets (images, fonts, etc.)
│   ├── api/                              # API-related services
│   ├── handlers/                         # Controller-like files for logic
│   ├── repositories/                     # Data fetching layer
│   ├── components/                       # Reusable React components
│   ├── modules/                          # Modular components
│   ├── templates/                        # Page templates
│   ├── pages/                            # Top-level components (views)
│   ├── react-routes/                     # React Router configuration
│   ├── redux/                            # Redux store and state management
│   ├── helpers/                          # Utility helper functions
│   └── utils/                            # Additional utilities
├── Dockerfile                            # Docker configuration for frontend
├── docker-compose.yml                    # Docker Compose file for frontend services
├── package.json                          # Node.js dependencies
└── .env.example                          # Environment variable example
```

---

## Prerequisites

Ensure you have the following installed before running the project:
- **Docker** and **Docker Compose**
- **Node.js** (for frontend)
- **Gradle** (for backend build automation)

## How to Run
### Create .env file in both `frontend/` and `backend/` follows the format of `.env.example`
### Using the Makefile

We provide a `Makefile` to simplify the process of running the project.

1. **Serve the Backend**:
   ```bash
   make serve-backend
   ```
   This will:
   - Navigate to the `backend` directory.
   - Build and start the backend services with Docker Compose.

2. **Serve the Frontend**:
   ```bash
   make serve-frontend
   ```
   This will:
   - Navigate to the `frontend` directory.
   - Build and start the frontend services with Docker Compose.

3. **Run Backend Tests**:
   ```bash
   make run-test
   ```
   This will:
   - Navigate to the `backend` directory.
   - Run the tests using Gradle (including `Jacoco` for code coverage).

---

## Environment Variables

Ensure you have appropriate environment variables set up in both `frontend/.env` and `backend/.env` for proper configuration. Example `.env` files are provided for each service:

- **Frontend**: `.env.example`
- **Backend**: `application.properties` and `.env` for MySQL credentials, JWT secret, etc.


## Testing

- The backend uses **JUnit** for testing and **Jacoco** for code coverage.
- To run tests, use:
  ```bash
  make run-test
  ```

## CI/CD Pipeline

We have set up **Jenkins** for continuous integration and deployment. Webhooks are configured to trigger builds from **GitHub** and send notifications to **Discord** upon each push.

---

## Contributors
- **hale0087**: Scrum master
- **ntra7383**: Product owner
- **btra8435**: developer
- **qngu0806**: developer
