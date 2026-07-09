# Architecture & Design Principles

## Core Architecture Rules

These rules define how the GeoRisk AI backend is structured and how components interact.

### 1. Controllers: Endpoints Only

**Rule**: Controllers expose REST API endpoints exclusively. Controllers do NOT contain business logic.

```java
@RestController
@RequestMapping("/api/locations")
public class LocationController {
    
    @Autowired
    private LocationService locationService;
    
    @PostMapping
    public ResponseEntity<LocationResponse> create(@RequestBody LocationCreateRequest request) {
        // Controllers only orchestrate and delegate to service layer
        LocationResponse response = locationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getById(@PathVariable Long id) {
        LocationResponse response = locationService.getById(id);
        return ResponseEntity.ok(response);
    }
}
```

**Benefits**: 
- Separation of concerns
- Easier to test business logic independently
- Controllers remain thin and maintainable

---

### 2. Services: Coordinate Business Logic

**Rule**: Services contain all business logic and coordinate between repositories, clients, and entities.

```java
@Service
public class LocationService {
    
    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private LocationMapper locationMapper;
    
    public LocationResponse create(LocationCreateRequest request) {
        // Validate input
        validateLocationRequest(request);
        
        // Business logic: check uniqueness
        if (locationRepository.existsByLatitudeAndLongitude(
            request.getLatitude(), 
            request.getLongitude())) {
            throw new LocationAlreadyExistsException("Location already exists");
        }
        
        // Create entity
        Location location = locationMapper.toEntity(request);
        
        // Persist
        Location saved = locationRepository.save(location);
        
        // Transform to response DTO
        return locationMapper.toResponse(saved);
    }
}
```

**Responsibilities**:
- Input validation
- Business rule enforcement
- Data transformation (Entity ↔ DTO)
- Transaction management
- Orchestrating multiple repositories

---

### 3. External API Calls: Client Classes

**Rule**: All external API calls must be isolated in dedicated client classes. Clients handle HTTP communication, retry logic, and error handling.

```java
@Component
public class WeatherClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${weather.api.key}")
    private String apiKey;
    
    @Value("${weather.api.baseUrl}")
    private String baseUrl;
    
    public WeatherApiResponse getWeather(double latitude, double longitude) 
            throws WeatherApiException {
        String url = String.format("%s?lat=%f&lon=%f&appid=%s", 
            baseUrl, latitude, longitude, apiKey);
        
        try {
            WeatherApiResponse response = restTemplate.getForObject(url, 
                WeatherApiResponse.class);
            return response;
        } catch (RestClientException e) {
            throw new WeatherApiException("Failed to fetch weather data", e);
        }
    }
}
```

**Benefits**:
- Centralized external API integration
- Easy to mock for testing
- Consistent error handling
- Single place to update API versions

---

### 4. Repositories: Persistence Only

**Rule**: Repositories handle database operations using Spring Data JPA. Repositories do NOT contain business logic.

```java
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    // Spring Data JPA auto-generates finder queries
    Location findByNameAndCountry(String name, String country);
    
    List<Location> findByCountry(String country);
    
    boolean existsByLatitudeAndLongitude(Double latitude, Double longitude);
    
    // Custom JPQL query for complex scenarios
    @Query("SELECT l FROM Location l " +
           "WHERE ST_Distance_Sphere(l.geometry, :point) < :radiusMeters")
    List<Location> findWithinRadius(@Param("point") Point point, 
                                     @Param("radiusMeters") double radius);
}
```

**Responsibility**:
- Database queries only
- No business logic
- Use Spring Data JPA conventions

---

### 5. DTO Mapping: External ↔ Internal

**Rule**: External API responses must be mapped to internal DTOs. Never expose external DTOs directly to clients or the frontend.

```java
// External API DTO (from OpenWeatherMap)
public class WeatherApiResponse {
    public double temp;
    public double humidity;
    public double wind_speed;
    // ... external fields
}

// Internal Request DTO
public class WeatherDataCreateRequest {
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    // ... internal fields
}

// Mapper
@Component
public class WeatherMapper {
    
    public WeatherDataCreateRequest fromWeatherApi(WeatherApiResponse apiResponse) {
        WeatherDataCreateRequest dto = new WeatherDataCreateRequest();
        dto.setTemperature(apiResponse.temp);
        dto.setHumidity(apiResponse.humidity);
        dto.setWindSpeed(apiResponse.wind_speed);
        return dto;
    }
}
```

**Benefits**:
- Decouples frontend from external API changes
- Enables data transformation/filtering
- Consistent internal API contracts

---

### 6. Risk Scoring Rules

**Rule**: Risk score calculation must be deterministic and testable. Risk scores are computed by dedicated scoring algorithms, not by AI.

**Principles**:
- **Deterministic**: Same input → Same output, always
- **Transparent**: Scoring reasons stored as JSON
- **Testable**: Algorithms are pure functions with no side effects
- **No AI**: Risk scores are NOT calculated by ML models

```java
@Component
public class RiskScoringService {
    
    public RiskAssessment assessRisk(Location location, 
                                     WeatherMeasurement weather,
                                     PoiSnapshot poi) {
        // Deterministic algorithm
        double score = calculateScore(location, weather, poi);
        String level = scoreToLevel(score);
        Map<String, Double> factors = extractFactors(location, weather, poi);
        
        RiskAssessment assessment = new RiskAssessment();
        assessment.setLocation(location);
        assessment.setScore(score);
        assessment.setLevel(level);
        assessment.setReasonsJson(factors); // Store contributing factors
        assessment.setAssessedAt(LocalDateTime.now());
        
        return assessment;
    }
    
    private double calculateScore(Location location, 
                                  WeatherMeasurement weather,
                                  PoiSnapshot poi) {
        // Transparent algorithm
        double temperatureRisk = calculateTemperatureRisk(weather.getTemperature());
        double precipitationRisk = calculatePrecipitationRisk(weather.getPrecipitation());
        double windRisk = calculateWindRisk(weather.getWindSpeed());
        double poiDensityRisk = calculatePoiDensityRisk(poi.getTotalCount());
        
        // Weighted combination
        double score = (temperatureRisk * 0.25) +
                      (precipitationRisk * 0.25) +
                      (windRisk * 0.15) +
                      (poiDensityRisk * 0.35);
        
        return Math.min(100, Math.max(0, score)); // Clamp 0-100
    }
}
```

**Testing Risk Scoring**:
```java
@Test
public void testRiskScoringDeterminism() {
    Location loc = createTestLocation();
    WeatherMeasurement weather = createTestWeather();
    PoiSnapshot poi = createTestPoi();
    
    double score1 = riskScoringService.assessRisk(loc, weather, poi).getScore();
    double score2 = riskScoringService.assessRisk(loc, weather, poi).getScore();
    
    assertEquals(score1, score2, "Risk scoring must be deterministic");
}
```

---

### 7. AI: Explanation Only

**Rule**: AI may only explain an already calculated risk score. AI must NOT influence risk score calculation. AI uses factual data only.

```java
@Service
public class AiExplanationService {
    
    @Autowired
    private RiskAssessmentRepository riskAssessmentRepository;
    
    @Autowired
    private MlModelClient mlModelClient; // External ML service
    
    public AiExplanation explainRiskScore(Long riskAssessmentId) 
            throws AiServiceException {
        // Fetch the already-calculated risk assessment
        RiskAssessment assessment = riskAssessmentRepository
            .findById(riskAssessmentId)
            .orElseThrow(() -> new AssessmentNotFoundException());
        
        // Prepare factual context
        ExplanationContext context = new ExplanationContext();
        context.setRiskScore(assessment.getScore());
        context.setRiskLevel(assessment.getLevel());
        context.setFactors(assessment.getReasonsJson());
        context.setLocation(assessment.getLocation());
        
        // Call AI to generate explanation (explanation only, not scoring)
        String explanation = mlModelClient.generateExplanation(context);
        
        // Store explanation
        AiExplanation aiExplanation = new AiExplanation();
        aiExplanation.setRiskAssessment(assessment);
        aiExplanation.setExplanation(explanation);
        aiExplanation.setModelName("gpt-4");
        aiExplanation.setGeneratedAt(LocalDateTime.now());
        
        return aiExplanationRepository.save(aiExplanation);
    }
}
```

**Key Constraints**:
- AI receives already-calculated scores
- AI uses only factual data from risk assessment
- AI cannot modify or override score
- AI output is for explanation, not prediction

---

### 8. MCP Server: Backend API Client

**Rule**: The MCP server is an external application that calls the backend REST API. It does NOT access the database directly.

```
┌──────────────────┐
│  MCP Server      │
│  (Python/Rust)   │
└────────┬─────────┘
         │
         │ REST API Calls
         │ HTTP/JSON
         ▼
┌──────────────────────┐
│  GeoRisk AI Backend  │
│  (Java Spring Boot)  │
└────────┬─────────────┘
         │
         │ SQL Queries
         │
         ▼
┌──────────────────┐
│  PostgreSQL +    │
│  PostGIS         │
└──────────────────┘
```

**Benefits**:
- Clear separation of concerns
- Backend remains independent
- MCP server is replaceable
- Easy to version API contract

---

### 9. Kafka: Event Pipeline (Future)

**Rule**: Kafka should only be integrated after the basic scheduled data collection flow works without events.

**Timeline**:
1. **Phase 1 (Sprint 1-5)**: Implement scheduled collection using Spring Scheduler
2. **Phase 2 (Sprint 6+)**: Refactor to Kafka for event-driven architecture

**Benefits of waiting**:
- Simpler initial design
- Validate domain logic before adding complexity
- Easier to test without async event handling
- Can introduce Kafka later without major refactoring

---

## Request Flow Diagram

### Typical API Request Flow

```
User Request
     │
     ▼
┌─────────────────────────────────────┐
│  HTTP GET /api/locations/{id}       │
└────────────────┬────────────────────┘
                 │
                 ▼
        ┌────────────────────┐
        │   Controller       │
        │  - Validate input  │
        │  - Route request   │
        └────────┬───────────┘
                 │
                 ▼
        ┌────────────────────────┐
        │   Service Layer        │
        │  - Apply business logic│
        │  - Coordinate workflow │
        └────────┬───────────────┘
                 │
        ┌────────┴─────────────┐
        │                      │
        ▼                      ▼
   ┌─────────────┐        ┌──────────────┐
   │ Repository  │        │ Client/Utils │
   │ - Query DB  │        │ - API calls  │
   └────┬────────┘        │ - Mapping    │
        │                 └──────┬───────┘
        ▼                        ▼
   ┌─────────────┐        ┌──────────────┐
   │  Database   │        │ External API │
   └─────────────┘        └──────────────┘
        │
        └────────────┬─────────────┘
                     │
                     ▼
        ┌────────────────────────┐
        │   Service Layer        │
        │  - Transform DTO       │
        │  - Aggregate data      │
        └────────┬───────────────┘
                 │
                 ▼
        ┌────────────────────┐
        │   Controller       │
        │  - Build response  │
        │  - Set status code │
        └────────┬───────────┘
                 │
                 ▼
        ┌────────────────────┐
        │  HTTP 200 OK       │
        │  Response JSON     │
        └────────┬───────────┘
                 │
                 ▼
            User Response
```

### Concrete Example: Get Risk Assessment

```
Request:
  GET /api/risk-assessments/123

1. Controller receives request
   RiskController.getById(123)

2. Controller delegates to service
   riskService.getById(123)

3. Service queries repository
   riskAssessmentRepository.findById(123)

4. Repository executes SQL
   SELECT * FROM risk_assessments WHERE id = 123

5. Service transforms entity to DTO
   RiskAssessmentResponse = map(entity)

6. Controller builds HTTP response
   ResponseEntity.ok(response)

Response:
  HTTP 200 OK
  {
    "id": 123,
    "locationId": 45,
    "score": 72.5,
    "level": "high",
    "reasons": {
      "temperature": 8.5,
      "precipitation": 6.2,
      "windSpeed": 3.1,
      "poiDensity": 54.7
    },
    "assessedAt": "2024-07-09T14:30:00Z"
  }
```

---

## Layering Summary

| Layer | Responsibility | Examples |
|-------|----------------|----------|
| **Presentation** | HTTP endpoints, validation | Controllers |
| **Business Logic** | Rules, orchestration, transformation | Services |
| **Integration** | External systems, APIs, clients | Clients, Mappers |
| **Persistence** | Database operations | Repositories |
| **Data** | Entities, DTOs | POJOs with annotations |

---

## Error Handling

All layers have consistent error handling:

```java
// Controller catches and transforms exceptions
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLocationNotFound(
        LocationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("LOCATION_NOT_FOUND", ex.getMessage()));
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));
    }
}
```

---

## Testing Strategy

- **Unit Tests**: Service layer tested with mocked repositories
- **Integration Tests**: Full stack with in-memory H2 database
- **Contract Tests**: API contracts validated with Restassured
- **Load Tests**: High-volume data processing scenarios
- **Coverage Goal**: >80% code coverage
