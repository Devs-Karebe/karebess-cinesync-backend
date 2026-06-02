# CineSync Backend

Backend API for CineSync - A movie group management system.

## Tech Stack

- **Java**: 21
- **Framework**: Spring Boot 3.5.14
- **Build Tool**: Maven
- **Database**: PostgreSQL 16
- **ORM**: Spring Data JPA + Hibernate
- **Migrations**: Flyway
- **API Documentation**: OpenAPI / Swagger
- **Observability**: Spring Boot Actuator + Micrometer (Prometheus)
- **Containerization**: Docker
- **Validation**: Jakarta Validation
- **Security**: Spring Security (JWT - to be implemented in Sprint 1)

## Architecture

The project follows a modular architecture organized by domain:

```
karebes.movies.backend
├── auth/              # Authentication and Authorization module
├── user/              # User Management module
├── movie/             # Movie Management module
├── group/             # Group Management module
├── raffle/            # Raffle Management module
├── shared/            # Shared components (responses, constants, utilities)
└── infrastructure/    # Infrastructure configuration (exception handling, configs)
```

## Prerequisites

- Java 21 or higher
- Maven 3.9 or higher
- Docker and Docker Compose
- PostgreSQL 16 (or use Docker Compose)

## Configuration

### Environment Variables

Copy the `.env.example` file to `.env` and configure your environment variables:

```bash
cp .env.example .env
```

Required variables:
- `SPRING_PROFILES_ACTIVE`: Spring profile (dev, prod, test)
- `DB_URL`: PostgreSQL connection URL
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password

### Profiles

The application supports the following Spring profiles:

- **dev**: Development environment with debug logging and SQL logging
- **prod**: Production environment with optimized logging and security
- **test**: Test environment using H2 in-memory database

## Running the Application

### Using Docker Compose (Recommended)

This is the easiest way to run the application with PostgreSQL:

```bash
# Start the application and database
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop the application
docker-compose down
```

### Running Locally

1. Start PostgreSQL (using Docker or local installation):

```bash
docker run -d \
  --name cinesync-postgres \
  -e POSTGRES_DB=cinesync_dev \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16-alpine
```

2. Run the application:

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

3. The application will start on `http://localhost:8080`

### Building the Application

```bash
# Build the project
./mvnw clean package

# Skip tests during build
./mvnw clean package -DskipTests
```

## API Documentation

Once the application is running, access the Swagger UI:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Health Checks and Metrics

The application exposes several endpoints for monitoring:

- **Health Check**: http://localhost:8080/actuator/health
- **Metrics (Prometheus)**: http://localhost:8080/actuator/prometheus
- **Info**: http://localhost:8080/actuator/info

## Database Migrations

Flyway is configured to manage database migrations:

- Migration files location: `src/main/resources/db/migration`
- Naming convention: `V{version}__{description}.sql`
- Flyway runs automatically on application startup

Example migration file: `V1__create_initial_schema.sql`

## Testing

### Run All Tests

```bash
./mvnw test
```

### Run Specific Test Class

```bash
./mvnw test -Dtest=BackendApplicationTests
```

### Test Structure

- `BaseUnitTest`: Base class for unit tests
- `BaseIntegrationTest`: Base class for integration tests with MockMvc
- Test profile uses H2 in-memory database

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/karebes/movies/backend/
│   │   │   ├── auth/
│   │   │   ├── user/
│   │   │   ├── movie/
│   │   │   ├── group/
│   │   │   ├── raffle/
│   │   │   ├── shared/
│   │   │   │   ├── constant/
│   │   │   │   └── response/
│   │   │   ├── infrastructure/
│   │   │   │   ├── config/
│   │   │   │   └── exception/
│   │   │   └── BackendApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       ├── logback-spring.xml
│   │       └── db/migration/
│   └── test/
│       ├── java/karebes/movies/backend/
│       │   ├── BaseUnitTest.java
│       │   └── BaseIntegrationTest.java
│       └── resources/
│           └── application-test.properties
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── pom.xml
└── README.md
```

## API Versioning

All API endpoints are versioned under `/api/v1`:

```
http://localhost:8080/api/v1/{resource}
```

## Standard API Responses

### Success Response

```json
{
  "timestamp": "2024-01-01 12:00:00",
  "status": 200,
  "message": "Success",
  "data": { ... }
}
```

### Error Response

```json
{
  "timestamp": "2024-01-01 12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "path": "/api/v1/resource/1"
}
```

### Validation Error Response

```json
{
  "timestamp": "2024-01-01 12:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid request parameters",
  "errors": {
    "field": "error message"
  },
  "path": "/api/v1/resource"
}
```

## Development Guidelines

### Adding New Endpoints

1. Create the controller in the appropriate domain module
2. Use the standard `ApiResponse<T>` for responses
3. Add appropriate validation annotations
4. Document the endpoint with OpenAPI annotations
5. Write unit and integration tests

### Adding New Migrations

1. Create a new SQL file in `src/main/resources/db/migration`
2. Follow the naming convention: `V{version}__{description}.sql`
3. Test the migration locally before committing

### Exception Handling

Use the provided exception classes:
- `ResourceNotFoundException`: For 404 errors
- `BusinessException`: For business rule violations

The `GlobalExceptionHandler` will automatically handle these exceptions.

## Future Deployment (OCI)

The application is containerized and ready for deployment to Oracle Cloud Infrastructure (OCI).

### OCI Deployment Steps (Future)

1. Build the Docker image
2. Push to OCI Container Registry
3. Deploy to OCI Container Engine for Kubernetes (OKE) or OCI Compute

## Security Considerations

- Never commit `.env` files to version control
- Use strong passwords in production
- Enable HTTPS in production
- Implement JWT authentication (Sprint 1)
- Configure CORS appropriately for your frontend

## Troubleshooting

### Database Connection Issues

- Ensure PostgreSQL is running
- Check connection string in `.env` or properties
- Verify database credentials

### Port Already in Use

- Change `SERVER_PORT` in `.env` or `application.properties`
- Or stop the process using port 8080

### Flyway Migration Failures

- Check migration file naming convention
- Verify SQL syntax
- Use `spring.flyway.clean=true` to reset (development only)

## License

MIT License

## Contact

- CineSync Team
- Email: contact@cinesync.com
- Website: https://cinesync.com
