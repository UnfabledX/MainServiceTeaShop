# TeaShop

The given project represents an online tea shop.
What is already done so far - project functionality:
1. The user can register and login
2. The user can renew link of email confirmation if the user was not quick enough to activate their account
3. The user can update information about themselves and their delivery information.
4. The user can see the list of products, add desired items to the cart.
5. The user can create order by adding products to the cart, change the quantity of items afterward and 
finally complete the order by checking the delivery options.
6. The user receives email letter with a short summary regarding their current order.
7. The user can see their previous orders made in the past.
8. The admin user (admin) can view all users and their information
9. Admin can ban or activate users, change user information and delivery options if necessary.
10. Admin can manipulate with products and its images - all CRUD operations
11. Admin can see the list of all orders and process orders in active status.
12. Admin receives email if someone creates new order.

Image processing occurs in the backend media-service
(https://github.com/UnfabledX/MediaServerTeaShop) which stores files in postgres database.
In the future all backend applications are going to communicate through eureka server.
In order to run application locally follow these steps:

1. Install manually Tomcat (I was using tomcat-10.1.11, oldest versions might not work because of incompatibility with
   new spring boot versions 3.+)
2. If you use Docker, run docker-compose file in the root folder of the project by command in
   terminal `docker-compose up -d --build`. This is the simplest way to start a postgres database.
3. Insert env variables for database connection and email sending in Tomcat configuration (In intellij
   Idea [Run/debug configurations] -> [Edit configurations...] -> Choose Tomcat server -> [Startup/Connections] -> Pass
   env variables)
4. Clone eureka server from https://github.com/UnfabledX/EurekaServerTeaShop and run it.
5. Clone media server from https://github.com/UnfabledX/MediaServerTeaShop
6. In the media server insert env variables for database connection in Run/debug configuration for run-class (
   MediaServerApplication.java)
7. Run media server.
8. Clone order server from https://github.com/UnfabledX/OrderServiceTeaShop
9. In the order server insert env variables for database connection in Run/debug configuration for run-class (
   OrderServiceApplication.java)
10. Run order server.
11. Run Tomcat in the teashop project.

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
- Thymeleaf (html, css)
- Bootstrap 5
- Maven
