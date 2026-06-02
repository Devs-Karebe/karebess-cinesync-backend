package karebes.movies.backend.shared.constant;

/**
 * API Constants
 * Contains constant values used across the application
 */
public class ApiConstants {

    private ApiConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // API Version
    public static final String API_V1_BASE_PATH = "/api/v1";

    // Headers
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Messages
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String RESOURCE_CREATED = "Resource created successfully";
    public static final String RESOURCE_UPDATED = "Resource updated successfully";
    public static final String RESOURCE_DELETED = "Resource deleted successfully";
    public static final String VALIDATION_ERROR = "Validation failed";
}
