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

```bash
# Clone the repository
git clone https://github.com/NimnaKs/Code-Challenge-1-BackEnd.git

# Navigate to the project directory
cd Code-Challenge-1-BackEnd

# Build and run the project
mvn clean install

## Project Structure

/src
|-- /main
    |-- /java
        |-- lk.ijse.codingchallengejavaee
            |-- /api
                |-- /Customer
                |-- /Item
                |-- /Order
            |-- /db
                |-- /CustomerDBProcess
                |-- /ItemDBProcess
                |-- /OrderDBProcess
                |-- /OrderDetailsDBProcess
            |-- /dto
                |-- /CustomerDTO
                |-- /ItemDTO
                |-- /OrderDTO
                |-- /OrderDetailsDTO
                |-- /CombinedOrderDTO
            |-- /filter
                |-- /CROSFilter
                |-- /SecurityFilter
    |-- /resources
        |-- /SQL
            |-- /ShopPossQueries.SQL
        |-- /logback.xml
    |-- /webapp
        |-- /META-INF
            |-- /context.xml
        |-- /WEB-INF
            |-- /web.xml
|-- /test



