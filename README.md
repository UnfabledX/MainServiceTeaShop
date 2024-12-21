# TeaShop
**TeaShop** is an online platform that serves as a digital tea shop. It allows users to explore, purchase, and manage tea products, with a variety of features for both end-users and administrators. Additionally, this project is structured as part of a microservices architecture.
## **Key Features**
### **User Features**
1. User registration and login.
2. Renewal of email confirmation links for activating accounts.
3. Management of personal and delivery information.
4. Exploration of a product catalog, with the ability to:
    - Add items to a cart.
    - Adjust quantities of items in the cart.
    - Create and finalize orders.

5. Automatic email notifications with summaries of their orders.
6. Access to their past orders.
7. Product filtering by categories (e.g., **Jams**, **Tea**, **Mushrooms**, **Herbs**, etc.).
8. Full-text product search (supports both **English** and **Ukrainian**).
9. Simplified navigation via a welcome page that links to product categories.
10. Access to a blog with content related to tea, herbs, jams, etc. (includes information about the blog owner and a contact form for inquiries).
11. View delivery and payment information.

### **Admin Features**
1. View and manage user accounts (e.g., ban/activate users, update user and delivery information).
2. Full **CRUD operations** for products and product images.
3. Synchronized **SAVE** and **UPDATE** operations:
    - Product data is updated in a **Google Sheet** via the **Google Drive API**.

4. Monitoring and processing of all orders (admin can handle "active" status orders).
5. Automatic notifications to the admin via email when new orders are created.

## **Microservices Infrastructure**
This project is part of a **microservices-based architecture**, consisting of the following components:
1. **Main-teashop** (this project).
2. **Order-teashop** (handles order-related services).
3. **Media-teashop** (manages image processing and storage via a dedicated backend service).
    - Stores files in a **Postgres database**.

4. **Blog-teashop** (a dedicated blog service).
5. **Eureka-teashop** (acts as a service registry for inter-service communication).

### **Additional Components**
- **Postgres Database**: Stores data for all the microservices.
- **Nginx Server**: Manages SSL certificates and routes incoming requests within the infrastructure.

## **Technology Stack**
The following technologies power the TeaShop platform:
### **Backend**
- **Java 17**
- **Spring Boot** (v3.4.0)
- **Spring MVC**
- **Spring Data JPA**
- **Spring Security**
- **Hibernate**
- **Postgres Database** with **Full-Text Search**
- **Docker** for containerization
- **Flyway** for database migrations
- Communication through **Eureka Server**

### **Frontend**
- **Thymeleaf** (HTML, CSS)
- **Bootstrap 5**

### **Integration & External Tools**
- **Google Drive API** for product data synchronization.
- **Google Sheet API** for managing product sheets.

## **Main Features and Flow**
1. The application uses **Thymeleaf templates** for its UI, styled with **Bootstrap** to ensure responsiveness and user-friendly design.
2. Image processing and media management are handled by the **media-teashop** microservice.
3. Services communicate with each other via **Eureka Server** for seamless operation within the microservices architecture.

## **Repository Overview**
This repository (`main-teashop`) covers the primary business logic for the TeaShop platform, including user management, product catalog handling, orders, and the administrative dashboard.
For the **media-teashop** service used for image processing, check [MediaServerTeaShop on GitHub]().