# Project Structure

## Repository Layout

```
GeoRisk-AI/
├── backend/                    # Java Spring Boot backend application
├── frontend/                   # React/Vue.js web frontend (planned)
├── ai/                         # Python AI/ML notebooks and models (planned)
├── mcp-server/                 # MCP server for tool integration (planned)
├── docs/                       # Project documentation
│   ├── project-structure.md   # This file
│   ├── database.md            # Database schema and design
│   ├── architecture.md        # System design principles
│   └── roadmap.md             # Development roadmap
├── docker/                     # Docker and Docker Compose files (planned)
├── README.md                   # Main project README
└── .gitignore                 # Git ignore rules
```

### Folder Descriptions

| Folder | Purpose |
|--------|---------|
| **backend/** | Java backend application with Spring Boot framework, REST APIs, business logic, and database access |
| **frontend/** | React or Vue.js web application for map visualization and risk dashboard (future) |
| **ai/** | Python Jupyter notebooks, ML models, and AI explanation services (future) |
| **mcp-server/** | MCP server implementation for tool integration and extensibility (future) |
| **docs/** | Documentation including architecture, database schema, and roadmap |
| **docker/** | Docker Compose configurations for local development and deployment |

---

## Backend Package Structure

The backend uses a **feature-based modular architecture** where each domain feature has its own folder containing controllers, services, repositories, entities, and DTOs. This approach improves maintainability, scalability, and testability compared to generic layered structures.

### Root Backend Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ro/
│   │   │       └── sergiu/
│   │   │           └── georiskai/
│   │   │               ├── GeoRiskAiApplication.java      # Spring Boot entry point
│   │   │               ├── common/                         # Shared utilities
│   │   │               ├── health/                         # Health check endpoints
│   │   │               ├── location/                       # Location management
│   │   │               ├── weather/                        # Weather data collection
│   │   │               ├── poi/                            # Points of Interest
│   │   │               ├── risk/                           # Risk scoring
│   │   │               └── ai/                             # AI explanations
│   │   └── resources/
│   │       ├── application.yml                            # Spring configuration
│   │       └── application-dev.yml                        # Development overrides
│   └── test/
│       ├── java/                                           # Unit and integration tests
│       └── resources/                                      # Test configuration
├── pom.xml                                                 # Maven dependencies
└── docker/                                                 # Backend Docker files
```

### Feature Module Structure (Example: Location Module)

```
location/
├── LocationController.java                  # REST endpoints
├── LocationService.java                     # Business logic
├── LocationRepository.java                  # Data persistence (Spring Data JPA)
├── Location.java                            # JPA entity
├── dto/
│   ├── LocationCreateRequest.java          # Request DTO
│   ├── LocationUpdateRequest.java          # Request DTO
│   ├── LocationResponse.java               # Response DTO
│   └── GeocodingResult.java                # External API response mapping
└── exception/
    └── LocationNotFoundException.java       # Domain-specific exception
```

### Core Modules

#### `common/`
Shared utilities and cross-cutting concerns:
- `exception/` - Custom exceptions (GlobalExceptionHandler)
- `validation/` - Input validation logic
- `config/` - Spring configuration (database, security, etc.)
- `util/` - Helper utilities (formatters, converters, etc.)

#### `health/`
System health and readiness checks:
- `HealthController.java` - GET /actuator/health endpoints
- Monitors database connectivity, external API availability
- Used for readiness probes in Kubernetes/Docker

#### `location/`
Geographic location management:
- **LocationController** - REST endpoints for CRUD operations
- **LocationService** - Business logic for location operations
- **LocationRepository** - Spring Data JPA for database access
- **Location** - JPA entity with geospatial coordinates
- **DTOs** - Request/response objects, geocoding result mapping
- Integrates with external geocoding APIs

#### `weather/`
Real-time weather data collection and storage:
- **WeatherController** - REST endpoints for weather queries
- **WeatherService** - Coordinates collection and storage
- **WeatherClient** - External weather API integration (HTTP client)
- **WeatherMeasurement** - JPA entity for storing historical data
- **WeatherMeasurementRepository** - Data persistence
- **DTOs** - External API response mapping to internal models
- Fetches data from OpenWeatherMap or similar provider

#### `poi/`
Points of Interest analysis:
- **PoiController** - REST endpoints for POI queries
- **PoiService** - POI retrieval and analysis logic
- **PoiClient** - OpenStreetMap Overpass API integration
- **DTOs** - Response formatting for POI data
- Analyzes density and types of POIs (restaurants, hospitals, etc.)

#### `risk/`
Urban risk assessment and scoring:
- **RiskController** - REST endpoints for risk assessment queries
- **RiskScoringService** - Risk calculation algorithms
- **RiskService** - Orchestrates risk assessment workflow
- **RiskAssessment** - JPA entity storing historical assessments
- **RiskAssessmentRepository** - Data persistence
- **DTOs** - Risk score response formatting
- Deterministic, testable scoring based on weather, POI, and location factors

#### `ai/`
AI-powered risk explanations (future):
- **AiExplanationService** - Generates explanations for risk scores
- **AiExplanation** - JPA entity storing explanation history
- **AiExplanationRepository** - Data persistence
- **DTOs** - Explanation response formatting
- Explains risk scores using factual data; does NOT calculate risk
- Uses ML models to interpret risk factors

### Future Backend Concerns

#### Kafka Integration (Future)
Not part of the main package structure initially. Will be added as:
- `infrastructure/kafka/` - Kafka producer/consumer configs
- Message topics for weather collection, risk events
- Event-driven architecture for high-volume data processing

#### MCP Server (Future)
Separate service or module:
- `mcp-server/` - Separate application calling backend APIs
- Does NOT access the database directly
- Exposes backend capabilities as MCP tools

---

## Naming Conventions

### Classes
- **Entity**: `User.java`, `Location.java` (JPA entities)
- **Controller**: `LocationController.java` (REST endpoints)
- **Service**: `LocationService.java` (business logic)
- **Repository**: `LocationRepository.java` (data access)
- **Request DTO**: `LocationCreateRequest.java`, `LocationUpdateRequest.java`
- **Response DTO**: `LocationResponse.java`
- **Exception**: `LocationNotFoundException.java`
- **Client**: `WeatherClient.java` (external API integration)
- **Mapper**: `LocationMapper.java` (conversion between DTOs and entities)

### Packages
- Always lowercase: `com.example.modulename`
- Feature-based: `ro.sergiu.georiskai.location`, `ro.sergiu.georiskai.weather`
- Sub-packages by concern: `ro.sergiu.georiskai.location.dto`, `ro.sergiu.georiskai.common.exception`

### Database
- Table names: lowercase, plural: `locations`, `weather_measurements`
- Column names: lowercase, underscore-separated: `location_id`, `created_at`
- Primary key: `id` (auto-increment Long)
- Foreign keys: `{entity_name}_id` (e.g., `location_id`)
- Timestamps: `created_at`, `updated_at`

---

## Development Workflow

### Adding a New Feature Module

1. Create folder under `backend/src/main/java/ro/sergiu/georiskai/{feature}/`
2. Create entity class: `{Feature}.java` with `@Entity` annotation
3. Create repository: `{Feature}Repository.java` extending `JpaRepository`
4. Create service: `{Feature}Service.java` with business logic
5. Create DTOs in `dto/` subfolder
6. Create controller: `{Feature}Controller.java` with `@RestController` annotation
7. Add exception handling in `common/exception/`
8. Write unit and integration tests
9. Update documentation

### Testing

- Unit tests: `src/test/java/ro/sergiu/georiskai/{feature}/*Test.java`
- Integration tests: Use `@SpringBootTest` with test database
- Test coverage: Aim for >80% code coverage per module

---

## Dependencies (Spring Boot)

Core dependencies (to be configured in `pom.xml`):
- Spring Boot Web (REST API)
- Spring Data JPA (database access)
- PostgreSQL JDBC driver
- PostGIS extension
- Jackson (JSON serialization)
- Lombok (boilerplate reduction)
- JUnit 5 & Mockito (testing)
- Restassured (API testing)
