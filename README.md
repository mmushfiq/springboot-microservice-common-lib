# Spring Boot Microservice Common Library

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)
![JDK](https://img.shields.io/badge/JDK-21-blue)
![Gradle](https://img.shields.io/badge/Gradle-8.6-orange)
![SpringDoc OpenAPI](https://img.shields.io/badge/springdoc--openapi-2.4.0-yellowgreen)
![OpenTelemetry](https://img.shields.io/badge/OpenTelemetry-1.37.0-purple)
![Checkstyle](https://img.shields.io/badge/Checkstyle-10.14.2-cyan)
![GitHub](https://img.shields.io/github/license/isopropylcyanide/Jwt-Spring-Security-JPA?color=blue)

A comprehensive common library built with Spring Boot 3.3 and Java 21, designed to streamline and standardize the development of microservices within your projects.

`{PN}` - _project name or project name abbreviation_

## Features

### Logging

- **Standardized Request/Response Logging**: Automatically logs incoming requests and outgoing responses based on the base project package. Provides options to exclude request or response bodies from logging when they are excessively large.

- **Event Logging**: Seamlessly logs producer and consumer events, enhancing traceability across microservices.

- **Error Context Logging**: Captures consumer errors by adding a specific MDC (Mapped Diagnostic Context) field with the value `EVENT_ERROR`. Similarly, logs errors related to methods annotated with `@Async` by adding an MDC field with the value `ASYNC_ERROR`.

### Feign Configuration

- **Unified Feign Client Configuration**: Allows all project-specific microservices to utilize a centralized `FeignConfig`, eliminating the need for individual configurations. This approach ensures consistent error decoding and facilitates the propagation of project-specific headers across microservices.

### OpenTelemetry Integration

- **Automatic Trace Exporting**: Automatically exports span and trace information for each microservice to OpenTelemetry, enabling effective tracking and monitoring of distributed systems.

### Swagger Integration

- **Springdoc OpenAPI Integration**: Integrates with Springdoc OpenAPI, allowing each microservice to document its APIs. Minimal configuration changes in `application.yaml` are required to display specific API details; otherwise, common API details are utilized.

### Global Error Handling

- **Comprehensive Error Handling**: The `CommonErrorHandler` addresses a wide range of common exceptions and provides a standardized error response model. It dynamically captures error codes for `MethodArgumentNotValidException` and `ConstraintViolationException`, ensuring uniform logging conventions for all exceptions. Microservices should extend the `CommonErrorHandler` in their respective error handlers to maintain consistency.

### Request Interceptor

- **Header Propagation and Logging**: Extracts all project-specific headers from incoming requests and sets them as Logstash MDC fields before processing the request. These fields are cleared after the request is processed. This mechanism allows for tracking logs using indexed fields; for instance, monitoring all logs associated with a specific customer using the `pn-customer-id` field.

### Messaging

- **Event Models and Retry Logic**: Provides common event-related models and implements retry logic to enhance the reliability of message-driven microservices.

### Utilities

- **Utility Classes**: Offers a collection of utility classes that can be leveraged across microservices to promote code reuse and consistency.

- **Standardized Configuration Files**: Includes common `checkstyle.xml` and `logback.xml` files. Housing these files in the common library ensures that microservices consume them in a read-only manner, preventing unauthorized modifications and maintaining uniformity across projects.

## Usage Configuration

To incorporate the common library into your project, add the following dependency:

### Gradle

```groovy
implementation "com.company.project:springboot-microservice-common-lib:${pnCommonLibVersion}"
```

### Maven

```xml
<dependency>
<groupId>com.company.project</groupId>
<artifactId>springboot-microservice-common-lib</artifactId>
<version>${pnCommonLibVersion}</version>
</dependency>
```


**Note:** This common library is not yet available in the central repository. To use it locally, clone this repository and execute `gradle publishToMavenLocal` to publish it to your local Maven repository.

### Bean Detection Configuration
To ensure that Spring detects and registers the beans provided by this common library, add the following class to the config package of your microservice:

```java
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.company.project.common")
public class CommonLibScannerConfig {
}
```
This will enable automatic scanning and registration of common library components.


## Reference Implementation
For a practical implementation example, refer to the Spring Boot microservice template that utilizes this common library:

[Spring Boot Microservice Template](https://github.com/mmushfiq/springboot-microservice-template)