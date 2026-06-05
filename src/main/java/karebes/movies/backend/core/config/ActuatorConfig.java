package karebes.movies.backend.core.config;


import org.springframework.context.annotation.Configuration;

/**
 * Actuator Configuration
 * Configures health checks, metrics, and monitoring endpoints
 * Note: Most Actuator configuration is done via application.properties
 * This class is reserved for custom actuator configurations if needed
 */
@Configuration
public class ActuatorConfig {

    // Actuator is auto-configured by Spring Boot
    // Custom health indicators or metrics can be added here in future sprints
}
