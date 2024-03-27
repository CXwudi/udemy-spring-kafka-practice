# Kafka Practice Project

This is the practice project I created as I followed the Kafka course on Udemy.
The course is called ["Introduction to Kafka with Spring Boot"](https://www.udemy.com/course/introduction-to-kafka-with-spring-boot) by [John Thompson](https://www.udemy.com/user/john-thompson-2/).

## What is different from the course [official sample project](https://github.com/lydtechconsulting/introduction-to-kafka-with-spring-boot)

- Kotlin instead of Java
- Gradle instead of Maven
- A common Gradle module to define the common Kafka topic names and Kafka message classes, shared by both `tracking-service` and `dispatcher-service`
- Kotest + Mockk + Kotest Spring Extension, instead of JUnit + Mockito
- Using a Docker Compose file to start Kafka, see [compose.kafka-dev.yml](docker/compose.kafka-dev.yml)
- Everything taught in the course is implemented, except the retry section

