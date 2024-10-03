# Task Management Application

This is a full-stack task management application built with React for the frontend and Spring Boot for the backend.

## Features

- **User Authentication:** Secure user login and registration with email and password validation.
- **Task Creation:** Create new tasks with titles, descriptions, due dates, and priorities.
- **Task Management:** View, edit, and delete tasks.
- **Task Filtering and Sorting:** Filter tasks by status, priority, and due date. Sort tasks by various criteria.
- **User-Friendly Interface:** Intuitive and easy-to-use interface for managing tasks.
- **Rate Limiting:** Implemented rate limiting to prevent abuse and ensure service availability (5 login attempts per minute).
- **Refresh Token:**  Enhanced security with refresh tokens for long-lived sessions.

## Technologies Used

**Frontend:**

- React (To be implemented)
- JavaScript (To be implemented)
- HTML (To be implemented)
- CSS (To be implemented)

**Backend:**

- Spring Boot
- Spring Data JPA
- Spring Security
- MySQL 
- JWT (JSON Web Token)
- Bucket4j (for Rate Limiting)
- Lombok

## Getting Started

### Prerequisites

- Node.js and npm (or yarn) installed (for frontend development)
- Java Development Kit (JDK) 22 installed
- MySQL installed and running

### Backend Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/TaskManagementApplication.git
   ```
2. **Navigate to the backend directory:**
   ```bash
   cd TaskManagementApplication
   ```
3. **Configure database connection details:**
   - Open the `src/main/resources/application.properties` file.
   - Update the following properties with your MySQL database credentials:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
     spring.datasource.username=your_database_username
     spring.datasource.password=your_database_password
     ```
4. **Build the application:**
   ```bash
   ./gradlew build
   ```
5. **Run the application:**
   ```bash
   java -jar build/libs/TaskManagementApplication-0.0.1-SNAPSHOT.jar
   ```

### Frontend Setup (To be implemented)

1. **Navigate to the frontend directory:**
   ```bash
   cd frontend
   ```
2. **Install dependencies:**
   ```bash
   npm install
   ```
3. **Start the development server:**
   ```bash
   npm start
   ```
4. **Access the application in your browser:**
   - Typically at `http://localhost:3000` or the port specified in your frontend configuration.

## Running with Docker

1. **Build the Docker image:**
   ```bash
   docker build -t task-management-app .
   ```
2. **Run the Docker container:**
   ```bash
   docker run -p 8080:8080 task-management-app
   ```
3. **Access the application in your browser:**
   -  `http://localhost:8080` 

## Testing

- Backend unit and integration tests are written using JUnit and Mockito. Run tests with:
  ```bash
  ./gradlew test
  ```

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and commit them.
4. Push your changes to your fork.
5. Submit a pull request.

## License

This project is licensed under the MIT License.
