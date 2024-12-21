# TeaShop

The given project represents an online tea shop.
What is already done so far - project functionality:
1. The user can register and login
2. The user can renew link of email confirmation if the user was not quick enough to activate their account
3. The user can update information about themselves and their delivery information.
4. The user can see the list of products, add desired items to the cart.
5. The user can create order by adding products to the cart, change the quantity of items afterward and 
finally complete the order by checking the delivery options.
6. The user receives an email letter with a short summary regarding their current order.
7. The user can see their previous orders made in the past.
8. The admin user (admin) can view all users and their information
9. Admin can ban or activate users, change user information and delivery options if necessary.
10. Admin can manipulate with products and its images - all CRUD operations
11. All SAVE, UPDATE operations with a product are duplicated on **Google Drive**, where there is a sheet with all products as well.
12. Admin can see the list of all orders and process orders in active status.
13. Admin receives email if someone creates new order.
14. The user can filter products by the types, i.e. JAMS, TEA, MUSHROOMS, HERBS etc.
15. The user can use a **search** field in the page headers. Search will find any occurrences in the products name or description. 
Search can be fulfilled in English and in Ukrainian as well.
16. The user can go directly to the specified type of product from the welcome page.
17. The user can visit the blog which cover topics regarding herbs, fermented teas, jams. 
The blog has sections about the owner of the blog and contact section where the user can ask the blogger any kind of questions.
18. The use can see payment and delivery section.

Image processing occurs in the backend media-service
(https://github.com/UnfabledX/MediaServerTeaShop) which stores files in postgres database.
All backend microservices communicate through eureka server.


This application is the one part of the whole infrastructure:
Microservices:
- main-teashop (this one)
- order-teashop
- media-teashop
- blog-teashop
- eureka-teashop
Separately:
- database 
- nginx server for SSL certificate and routing requests

### Technology used in the project:

- Java 17
- Spring Boot 3.4.0
- Spring MVC
- Spring Data
- Spring Security
- Hibernate
- Postgres database
- Postgres full text search
- Docker
- Flyway
- Thymeleaf (html, css)
- Bootstrap 5
- Maven
- Google Drive, Sheet API
