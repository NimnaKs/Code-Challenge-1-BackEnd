# Point of Sale System - Java EE Backend

This project is a Java EE-based backend for a Point of Sale (POS) system. It includes functionalities related to managing Customers, Items, and Orders.

## Table of Contents

- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Database Integration](#database-integration)
- [Logging](#logging)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Introduction

This backend project serves as the core component of a Point of Sale system. It manages customer information, item details, and order processing. The APIs provided in this Java EE application enable CRUD operations and essential validations for maintaining a seamless POS system.

## Prerequisites

Ensure you have the following prerequisites installed before running the project:

- [Java](https://www.oracle.com/java/technologies/javase-downloads.html): This project requires Java to run. You can download it [here](https://www.oracle.com/java/technologies/javase-downloads.html).

- [Java EE](https://jakarta.ee/specifications/): The project is built on Java EE specifications. Ensure you have a compatible Java EE application server installed. You can use [WildFly](https://wildfly.org/) or [Tomcat](http://tomcat.apache.org/).

- [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/): The project uses MySQL as the database, and you need the MySQL Connector/J. You can download it [here](https://dev.mysql.com/downloads/connector/j/).

- [Lombok](https://projectlombok.org/): Lombok is used for reducing boilerplate code in the project. Make sure your IDE supports Lombok or install the Lombok plugin. You can find Lombok [here](https://projectlombok.org/).

- [Parsson](https://projects.eclipse.org/projects/technology.parsson): Parsson is used for JSON processing. You can find more about Parsson [here](https://projects.eclipse.org/projects/technology.parsson).

- [Yasson](https://eclipse-ee4j.github.io/yasson/): Yasson is used for JSON processing. You can find more about Yasson [here](https://eclipse-ee4j.github.io/yasson/).

- [SLF4J](http://www.slf4j.org/): SLF4J is used for logging. You can find more about SLF4J [here](http://www.slf4j.org/).

- [Logback](http://logback.qos.ch/): Logback is used for logging. You can find more about Logback [here](http://logback.qos.ch/).

## Getting Started

Follow these steps to set up and run the project locally:

**Clone the repository**
```
git clone https://github.com/NimnaKs/Code-Challenge-1-BackEnd.git
```

**Navigate to the project directory**
```
cd Code-Challenge-1-BackEnd
```

**Build and run the project**
```
mvn clean install
```

**Continue with the following steps:**

  **Enter SQL Script:**

  - Execute the SQL script provided in the SQL folder to initialize the database.
    
  **Database Configuration:**

  - Configure your database username and password in the context.xml configuration file.

  **Start the Application:**

  - Deploy the application on your Java EE server.
  
  **Access the API:**

  - The API will be accessible at http://localhost:your-port/api.

**Note:**

 - Ensure you have Java and Java EE installed.
 - Adjust the database configuration based on your setup.
 - Now you should have the backend up and running locally. For any additional details, refer to the provided documentation.

## Project Structure

The project follows a modular structure, organized into packages based on functionality.

- **api**
  - **Customer**: Contains classes related to Customer API.
  - **Item**: Contains classes related to Item API.
  - **Order**: Contains classes related to Order API.

- **db**
  - **CustomerDBProcess**: Database operations related to Customer.
  - **ItemDBProcess**: Database operations related to Item.
  - **OrderDBProcess**: Database operations related to Order.
  - **OrderDetailsDBProcess**: Database operations related to OrderDetails.

- **dto**
  - **CustomerDTO**: Data Transfer Object for Customer.
  - **ItemDTO**: Data Transfer Object for Item.
  - **OrderDTO**: Data Transfer Object for Order.
  - **OrderDetailsDTO**: Data Transfer Object for OrderDetails.
  - **CombinedOrderDTO**: Combined DTO for Order and its details.

- **filter**
  - **CROSFilter**: Cross-Origin Resource Sharing filter.
  - **SecurityFilter**: Placeholder for security-related filtering.

- **resources**
  - **SQL**: SQL scripts for initializing the database.
    - **ShopPossQueries.SQL**: Contains queries for setting up the database.
  - **logback.xml**: Configuration file for logging.

- **webapp**
  - **META-INF**: Contains the `context.xml` file for configuring the JNDI data source.
  - **WEB-INF**: Web application configuration.
    - **web.xml**: Deployment descriptor for the web application.

- **test**
  - Placeholder for test-related classes (unit tests, integration tests).

## Configuration

### Database Configuration

1. **Database System:** MySQL
2. **Schema Initialization:**
   - Execute the SQL script provided in the [SQL folder](src/main/resources/SQL) to initialize the database.

3. **Database Connection:**
   - Configure your database username and password in the `src/resources/META-INF/context.xml` file.
     
   #### context.xml

    ```
    xml
    <!-- context.xml -->
    <Resource auth="Container" driverClassName="com.mysql.cj.jdbc.Driver"
        maxActive="20" maxIdle="5" maxWait="10000"
        name="jdbc/posDB" password="your-db-password" type="javax.sql.DataSource"
        url="jdbc:mysql://localhost:3306/posdb" username="your-db-username"/>
    ```

### Application Properties

   - The project uses the `origin` parameters in the `web.xml` file to control Cross-Origin Resource Sharing (CORS). CORS is a security feature implemented by web browsers that restricts web pages from making requests to a different domain than the one that served the web page.

## Api Endpoints

Explore and interact with the API using [Postman](https://www.postman.com/avionics-astronaut-49946802/workspace/javaee-backend-poss/collection/30946779-1ee524ac-bc3e-4d75-af3d-e9063f97837b?action=share&creator=30946779)

## Database Integration

The project uses MySQL as the database. Execute the SQL script provided in the `SQL` folder to initialize the database.

## Logging

The application utilizes SLF4J and Logback for logging. You can configure the logging in the `logback.xml` file.

## Testing

The `test` directory contains placeholders for test-related classes, including unit tests and integration tests.

## Contributing

Feel free to contribute to the project. Fork the repository, make your changes, and submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE).
    





