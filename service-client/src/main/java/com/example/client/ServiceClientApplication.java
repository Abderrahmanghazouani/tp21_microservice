package com.example.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Avec spring-cloud-starter-netflix-eureka-client dans le classpath, l'enregistrement est automatique
@SpringBootApplication
public class ServiceClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceClientApplication.class, args);
    }
}
