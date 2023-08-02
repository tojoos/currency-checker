# Spring Boot & Angular Application

This project is a Spring Boot and Angular application that requires a MySQL database running on localhost:3306. It provides a web-based user interface for interacting with the application.

## Requirements

Before running the application, make sure you have the following software installed on your system:

- Java Development Kit (JDK) 17 (or any version compatible with Spring Boot 3.1.1)
- Node.js (v16.20.1 recommended)
- Angular CLI (v16.1.5 recommended)
- MySQL Server (running on localhost:3306) with appropriate database setup

# How to Run

Follow these steps to run the Spring Boot & Angular application:

## 1. **Clone the Repository**

First, clone this repository to your local machine using the following command:
```bash
git clone https://github.com/tojoos/currency-checker.git
```

## 2. **Backend Setup**

a. **Import Project into IDE**

Import the Spring Boot project into your preferred Integrated Development Environment (IDE) such as Eclipse or IntelliJ IDEA.

b. **Build and Run Backend**

If you're using Maven, navigate to the root directory of the Spring Boot project and run the following command:

```bash
mvn spring-boot:run
```


The backend will run on a default port - 8080.

## 3. **Frontend Setup**

Open a terminal or command prompt and navigate to the `client` directory inside the cloned repository.

```bash
cd spring-boot-angular-app/client
```

## a. **Install Dependencies**

Install the required Node.js packages using npm.

```bash
npm install
```

## b. **Run Frontend**

Start the Angular development server.
```bash
ng serve
```

The frontend application will run on http://localhost:4200.

## 4. **MySQL Database**

Ensure that your MySQL database server is running on `localhost:3306`, and create a database for this application. Remember to export appropriate environment variables to enable connection with database, Spring Boot application uses following:
```bash
# MySQL Config
spring.datasource.username=${MYSQL_LOGIN}
spring.datasource.password=${MYSQL_PASSWORD}
```


## Additional Notes

- The application's frontend will communicate with the backend API running on `http://localhost:8080`. Make sure the backend is running before accessing the frontend.
- You can customize the backend API base URL in the frontend application if your backend is running on a different port or domain.
- The application is tested and verified on Windows 64-bit (win32 x64) operating system. It should also work on other platforms, but it is recommended to use Windows for the best compatibility.
- There is also a `import.sql` file that contains sample data that can be loaded into databse if needed.
- Application reuses same db without removing any data when closing, you can change this behaviour by manipulating properties in `application.properties` file:
```
# JPA Config
# Create a db on startup (change to create for data migration)
spring.jpa.hibernate.ddl-auto=update
```

Feel free to reach out if you encounter any issues or have questions related to the application.
