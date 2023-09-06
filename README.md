# TeaShop
The given project represents an online tea shop. 
For now Simple CRUD operation for products are implemented. 
Each product can have an image. Image processing occurs in the backend media-service 
(https://github.com/UnfabledX/MediaServerTeaShop) which stores files in postgres database. 
In the future all backend applications are going to communicate through eureka server. 
In order to run application follow these steps:
1. Install manually Tomcat (I was using tomcat-10.1.11, oldest versions might not work because of incompatibility with new spring boot versions 3.+)
2. If you use Docker, run docker-compose file in the root folder of the project by command in terminal `docker-compose up -d --build`. This is the simplest way to start a postgres database.
3. Insert env variable for database connection in Tomcat configuration (In intellij Idea [Run/debug configurations] -> [Edit configurations...] -> Choose Tomcat server -> [Startup/Connections] -> Pass env variables)
4. Clone media server from https://github.com/UnfabledX/MediaServerTeaShop
5. In the media server insert env variable for database connection in Run/debug configuration for run-class (MediaserverApplication.java)
6. Run media server.
7. Clone order server from https://github.com/UnfabledX/OrderService
8. In the order server insert env variable for database connection in Run/debug configuration for run-class (OrderServiceApplication.java)
9. Run order server.
10. Run Tomcat in the teashop project.

Features that are going to be implemented in the future:
1. User authentication and authorization. (done)
2. Orders processing (in progress)
3. Views
4. Probably main entities will be split between microservices (so there will be like user-service, Order service, auth server etc.)
5. And a lot of different features that will appear during application development.

Technology used in the project: 
- Java 17
- Spring Boot
- Spring MVC
- Spring Data
- Spring Security
- Hibernate
- Postgres database
- Docker
- Flyway
- Thymeleaf (html, css a bit)
- Bootstrap 5
- Maven
