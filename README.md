Paul Backend Test 1 - Array
Technology Overview
    - The application uses Angular 9 for the web front end 
    - The backend is a Spring Boot Java app containing the REST API and utilizes liquibase for the database management
    - Currently utilizing a MySQL database but that is interchangable with liquibase
    - Dockerfile for angular 9 and java app
    - docker-compose combines the above and a mysql docker instance on port 3307 in case you have one locally running on 3306

User Management
    - You can register a User with the front end 
    - Once registered and logged in you can perform CRUD operations on Users

Security Measures
    - CSRF token
    - Credentials auth header for certain REST endpoints
    - Encryption throughout frontend, backend and database
    - Docker secrets for mysql DB
    
REST Endpoints
1. /rest/users/register

Startup Procedures
1. docker-compose build
2. docker-compose up

TODOs - if more time
1. Implement an OAuth2 server to run alongside instead of the existing auth structure
2. Create unit tests 
3. Create automated UI test scripts
4. setup docker secrets not from file
5. disable unsafe protocols and insecure ciphers 



