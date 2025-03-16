# My Fullstack Application. It is just an assignment.

This repository contains a Spring Boot backend and a React frontend.

## Running the Backend (Spring Boot)

1. Navigate to the `backend-spring-boot-app/` directory.
2. Build the project using Maven:
   - `mvn clean install` (for Maven)
3. Run the application for Windows:
   - If Java is not installed, you can set the JAVA_HOME environment variable temporarily for the current Command Prompt session in Windows like this:
      1.	set JAVA_HOME= C:\path\to\java17
      2.	"%JAVA_HOME%\bin\java" -jar assignment.maybank-0.0.1.jar

   
The backend will be running at `http://localhost:8089`.

## Running the Frontend (React)

1. Navigate to the `frontend-react-app/` directory.
2. Install http-server globally
    - `npm install -g http-server`
4. Install dependencies:
   - `npm install`
5. Build the application
   - `npm run build`
6. After completing the build, navigate to the build directory (inside your project folder) containing the production-ready files.
   - `cd build`
7. Run the development server:
   - `http-server -p 3000`

The frontend will be running at `http://localhost:3000`.
