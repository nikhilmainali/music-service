package com.musicservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Music Service Spring Boot application.
 *
 * Concepts for beginners:
 * - @SpringBootApplication: A convenience annotation that enables:
 *   1. @Configuration: Tags the class as a source of bean definitions for the application context.
 *   2. @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings.
 *   3. @ComponentScan: Tells Spring to look for other components, configurations, and services
 *      in the 'com.musicservice' package and sub-packages.
 */
@SpringBootApplication
public class MusicServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicServiceApplication.class, args);
    }
}
