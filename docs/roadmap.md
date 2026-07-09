# Development Roadmap

GeoRisk AI development is organized into 15 sequential epics, each building on the previous work. The roadmap is designed to validate assumptions early and deliver incrementally.

---

## Epic 0: Initial Design and Documentation ✓

**Status**: IN PROGRESS  
**Duration**: 1 week  
**Goal**: Establish clear project vision, architecture, and implementation plan before coding.

### Tasks
- [x] Create README with project overview
- [x] Design database schema (MVP + extended)
- [x] Document architecture principles
- [x] Plan backend package structure
- [x] Create implementation roadmap
- [x] Set up Git repository with .gitignore

### Testing
- Code review of all documentation
- Verify database schema relationships
- Validate architecture rules against examples

### Learning
- Understand geospatial concepts (latitude, longitude, PostGIS)
- Review Spring Boot best practices
- Study feature-based architecture patterns

---

## Epic 1: Basic Java Backend

**Status**: PLANNED  
**Duration**: 1 week  
**Goal**: Create functional Spring Boot application with basic structure and health checks.

### Tasks
- [ ] Initialize Maven project with Spring Boot 3.x
- [ ] Configure application.yml (database, logging, server)
- [ ] Set up Spring Data JPA with PostgreSQL driver
- [ ] Create `Location` entity and `LocationRepository`
- [ ] Create `LocationController` with GET/POST endpoints
- [ ] Create `LocationService` with CRUD logic
- [ ] Add global exception handling
- [ ] Set up logging with SLF4J
- [ ] Create health check endpoint (/actuator/health)
- [ ] Add basic input validation
- [ ] Write unit tests for LocationService
- [ ] Write integration tests for LocationController

### Testing
- Start embedded Spring Boot application
- Test POST /api/locations (create)
- Test GET /api/locations (list)
- Test GET /api/locations/{id} (read)
- Test error handling (400, 404, 500)
- Health check returns UP

### Learning
- Spring Boot dependency injection
- Spring Data JPA query methods
- REST API design patterns
- Exception handling in Spring
- Unit vs. integration testing strategies

---

## Epic 2: Weather API Integration

**Status**: PLANNED  
**Duration**: 1 week  
**Goal**: Integrate external weather data API (OpenWeatherMap or similar).

### Tasks
- [ ] Create `WeatherClient` class for API communication
- [ ] Add REST client configuration (RestTemplate or Webclient)
- [ ] Configure API key management via environment variables
- [ ] Create `WeatherApiResponse` DTO for external API
- [ ] Create internal `WeatherDataCreateRequest` DTO
- [ ] Create `WeatherMapper` to transform external → internal
- [ ] Create `WeatherMeasurement` entity
- [ ] Create `WeatherMeasurementRepository`
- [ ] Create `WeatherService` for business logic
- [ ] Create `WeatherController` with endpoints
- [ ] Add retry logic for API failures
- [ ] Add circuit breaker pattern (optional: Spring Cloud)
- [ ] Write unit tests for WeatherClient (mocked API)
- [ ] Write integration tests with mock weather API

### Testing
- Call weather API for sample locations
- Verify data transformation (external → internal)
- Test error handling (API timeout, 5xx errors)
- Test retry logic with simulated failures
- Verify measurements stored in database

### Learning
- HTTP client libraries (RestTemplate)
- External API integration patterns
- DTO mapping strategies
- Error handling for external systems
- Configuration management (environment variables)

---

## Epic 3: Geocoding Integration

**Status**: PLANNED  
**Duration**: 1 week  
**Goal**: Add geocoding capability to resolve addresses into coordinates.

### Tasks
- [ ] Create `GeocodingClient` for geocoding API
- [ ] Integrate with geocoding service (Google Maps, OpenStreetMap)
- [ ] Create `GeocodingResult` DTO for API response
- [ ] Create `GeocodingMapper` to transform results
- [ ] Add geocoding endpoint to `LocationController`
- [ ] Implement reverse geocoding (coordinates → address)
- [ ] Add validation to prevent duplicate coordinates
- [ ] Cache geocoding results (optional)
- [ ] Write unit tests for GeocodingClient
- [ ] Write integration tests for geocoding endpoint

### Testing
- Test forward geocoding (address → coordinates)
- Test reverse geocoding (coordinates → address)
- Test API error handling
- Verify coordinates stored correctly in Location
- Test duplicate prevention

### Learning
- Geocoding service providers
- Caching strategies
- Coordinate validation
- Address parsing

---

## Epic 4: PostgreSQL and PostGIS Persistence

**Status**: PLANNED  
**Duration**: 1 week  
**Goal**: Set up production database with geospatial capabilities.

### Tasks
- [ ] Install PostgreSQL locally
- [ ] Install PostGIS extension
- [ ] Create database initialization scripts
- [ ] Configure Spring Boot to connect PostgreSQL
- [ ] Create migration scripts (Flyway or Liquibase)
- [ ] Implement MVP schema tables:
  - [ ] locations
  - [ ] weather_measurements
  - [ ] risk_assessments
- [ ] Add geospatial queries to LocationRepository
- [ ] Implement distance-based location queries
- [ ] Set up database indexes for performance
- [ ] Add connection pooling (HikariCP)
- [ ] Write integration tests with real PostgreSQL

### Testing
- Start PostgreSQL container (Docker recommended)
- Verify all tables created
- Test geospatial queries (distance, radius search)
- Test indexes on time-series data
- Performance test with 10K locations

### Learning
- PostgreSQL setup and configuration
- PostGIS geospatial types and functions
- Database migration tools
- Performance optimization (indexes, queries)
- Connection pooling

---

## Epic 5: Save Locations and Weather Measurements

**Status**: PLANNED  
**Duration**: 1 week  
**Goal**: Build complete CRUD workflow for locations and weather data.

### Tasks
- [ ] Implement POST /api/locations (create with geocoding)
- [ ] Implement PUT /api/locations/{id} (update)
- [ ] Implement DELETE /api/locations/{id} (soft delete)
- [ ] Implement GET /api/locations (list with pagination)
- [ ] Implement GET /api/locations/nearby (radius search)
- [ ] Create `LocationDTO` for responses (DTO layer)
- [ ] Implement POST /api/weather/fetch/{locationId}
- [ ] Implement GET /api/weather/{locationId}/latest
- [ ] Implement GET /api/weather/{locationId}/history (time range)
- [ ] Add request validation with Bean Validation
- [ ] Add pagination support
- [ ] Add sorting by location name, date
- [ ] Write comprehensive integration tests
- [ ] Document API with Swagger/OpenAPI

### Testing
- Create 100+ locations across different countries
- Fetch weather for each location
- Test pagination (page size, page number)
- Test sorting (by name, by date)
- Test time-range queries (last 7 days)
- Performance test with large datasets

### Learning
- Request/Response DTOs
- Pagination and sorting
- Bean Validation (JSR-380)
- API documentation with Swagger
- Query optimization for time-series data

---

## Epic 6: OpenStreetMap Points of Interest

**Status**: PLANNED  
**Duration**: 1 week  
**Goal**: Integrate POI data to understand urban context and density.

### Tasks
- [ ] Create `PoiClient` for Overpass API (OpenStreetMap)
- [ ] Create `PoiSnapshot` entity
- [ ] Create `PoiSnapshotRepository`
- [ ] Create `PoiService` for POI analysis
- [ ] Create `PoiController` with endpoints
- [ ] Implement POI retrieval by radius and type
- [ ] Analyze POI density (count, distance to nearest)
- [ ] Support multiple POI types:
  - [ ] Hospitals
  - [ ] Schools
  - [ ] Restaurants
  - [ ] Parks
  - [ ] Police stations
- [ ] Cache POI results with timestamp
- [ ] Write unit tests for PoiClient (mock Overpass API)
- [ ] Write integration tests for POI endpoints

### Testing
- Query POIs for major cities
- Verify density calculations
- Test caching behavior
- Test different POI types
- Verify nearest-POI distance calculation
- Performance test with complex POI queries

### Learning
- Overpass API query language
- Spatial analysis (density, distance)
- Caching strategies
- Data aggregation and analysis

---

## Epic 7: Urban Risk Score Calculation

**Status**: PLANNED  
**Duration**: 1.5 weeks  
**Goal**: Implement core risk scoring algorithm.

### Tasks
- [ ] Create `RiskAssessment` entity
- [ ] Create `RiskAssessmentRepository`
- [ ] Design risk scoring algorithm:
  - [ ] Temperature risk component
  - [ ] Precipitation risk component
  - [ ] Wind speed risk component
  - [ ] POI density risk component
- [ ] Implement `RiskScoringService`
- [ ] Risk score = weighted combination of factors
- [ ] Implement risk levels (low, medium, high, critical)
- [ ] Store risk factors as JSON for explainability
- [ ] Create `RiskController` endpoints
- [ ] Implement GET /api/risk/{locationId}/latest
- [ ] Implement GET /api/risk/{locationId}/history
- [ ] Validate algorithm output (0-100 range)
- [ ] Write unit tests for scoring logic (deterministic)
- [ ] Write integration tests for risk API

### Testing
- Test scoring determinism (same input = same output)
- Test boundary conditions (0%, 100%, edge values)
- Test weighted factor combinations
- Test risk level categorization
- Test JSON factors storage and retrieval
- Create test matrix with various weather/POI combinations

### Learning
- Risk modeling and scoring algorithms
- Weighted combination of factors
- Deterministic algorithm testing
- JSON storage and retrieval
- Domain knowledge on urban risk factors

---

## Epic 8: Frontend Map Interface

**Status**: PLANNED  
**Duration**: 2 weeks  
**Goal**: Build web UI for visualizing locations and risk scores.

### Tasks
- [ ] Initialize React or Vue.js project
- [ ] Set up Leaflet or Mapbox for mapping
- [ ] Create map component
- [ ] Display locations as markers on map
- [ ] Color-code markers by risk level (green → red)
- [ ] Implement location search and filtering
- [ ] Display location details popup
- [ ] Display weather data panel
- [ ] Display risk score with contributing factors
- [ ] Implement location creation form
- [ ] Add date range picker for historical data
- [ ] Deploy frontend with backend API
- [ ] Write component tests
- [ ] Add accessibility features

### Testing
- Test map rendering with 1000+ locations
- Test marker filtering by risk level
- Test location detail popup
- Test form validation
- Test API integration
- Test responsive design on mobile

### Learning
- React or Vue.js fundamentals
- Leaflet/Mapbox API
- Map interactions and user experience
- Frontend-backend integration
- API client library usage

---

## Epic 9: Scheduled Data Collection

**Status**: PLANNED  
**Duration**: 1 week  
**Goal**: Implement automated periodic data collection.

### Tasks
- [ ] Create `ScheduledCollectionService`
- [ ] Use Spring Scheduler (@Scheduled annotation)
- [ ] Implement collection job for weather data
- [ ] Implement collection job for POI snapshots
- [ ] Implement collection job for risk scoring
- [ ] Create configurable collection intervals (env variables)
- [ ] Add collection job status monitoring
- [ ] Add logging for collection runs
- [ ] Implement error recovery for failed collections
- [ ] Add metrics/telemetry (count, duration, errors)
- [ ] Write unit tests for scheduling logic
- [ ] Write integration tests for full collection flow

### Testing
- Run scheduler for 1 hour with 15-minute intervals
- Verify data collected for all monitored locations
- Test error handling (API failure, database error)
- Test recovery after transient failures
- Monitor job duration and success rate
- Test configuration changes

### Learning
- Spring Scheduler
- Task scheduling patterns
- Error recovery and resilience
- Observability and monitoring
- Job orchestration

---

## Epic 10: Kafka Data Pipeline

**Status**: PLANNED  
**Duration**: 2 weeks  
**Goal**: Migrate from scheduled polling to event-driven Kafka pipeline.

### Tasks
- [ ] Set up Kafka locally (Docker)
- [ ] Create Kafka topics:
  - [ ] weather-data-collected
  - [ ] poi-data-collected
  - [ ] risk-assessed
- [ ] Create Kafka producers in WeatherService, PoiService, RiskService
- [ ] Create Kafka consumers for risk assessment workflow
- [ ] Migrate from Spring Scheduler to Kafka consumers
- [ ] Implement message serialization (JSON)
- [ ] Add error handling and dead-letter queues
- [ ] Implement message retry logic
- [ ] Add monitoring and metrics (count, lag, errors)
- [ ] Write unit tests for producers/consumers
- [ ] Write integration tests with embedded Kafka

### Testing
- Publish weather-collected events
- Verify risk assessment triggered by event
- Test message ordering (single partition)
- Test error handling (poison pills)
- Load test (1000 messages/second)
- Test Kafka failure recovery

### Learning
- Kafka concepts (topics, partitions, offsets)
- Kafka producer/consumer patterns
- Message-driven architecture
- Distributed system concepts
- Monitoring event pipelines

---

## Epic 11: AI Risk Explanations

**Status**: PLANNED  
**Duration**: 2 weeks  
**Goal**: Add AI-powered natural language explanations for risk scores.

### Tasks
- [ ] Create `AiExplanation` entity
- [ ] Create `AiExplanationRepository`
- [ ] Create `AiExplanationService`
- [ ] Integrate with LLM API (OpenAI, Anthropic, local)
- [ ] Create prompt templates for explanations
- [ ] Implement POST /api/explanations/{riskAssessmentId}
- [ ] Implement GET /api/explanations/{locationId}
- [ ] Store explanations with model name and timestamp
- [ ] Add caching for identical assessment explanations
- [ ] Implement streaming responses (optional)
- [ ] Add cost tracking for AI API calls
- [ ] Write unit tests for explanation generation
- [ ] Write integration tests with mocked AI API

### Testing
- Test explanation generation for various risk levels
- Test consistency of explanations
- Test error handling (AI API timeout)
- Test caching behavior
- Test cost tracking

### Learning
- LLM API integration
- Prompt engineering
- Streaming API responses
- Cost optimization for AI services
- Ethical AI usage

---

## Epic 12: MCP Server Integration

**Status**: PLANNED  
**Duration**: 1.5 weeks  
**Goal**: Create MCP server for tool ecosystem integration.

### Tasks
- [ ] Design MCP server capabilities
- [ ] Create MCP server project (Python or Rust)
- [ ] Implement MCP tools:
  - [ ] assess_risk(location)
  - [ ] get_weather(location)
  - [ ] find_nearby_locations(radius)
  - [ ] explain_risk(assessment_id)
- [ ] Implement backend API client in MCP server
- [ ] Add error handling for API failures
- [ ] Implement tool argument validation
- [ ] Write tests for MCP tools
- [ ] Document tool schemas

### Testing
- Test each MCP tool independently
- Test tool chaining (multiple tools in sequence)
- Test error handling
- Test with various LLM clients
- Test tool discovery

### Learning
- MCP protocol specification
- Tool schema definition
- Backend API client design
- Multi-language integration

---

## Epic 13: Machine Learning Prediction

**Status**: PLANNED  
**Duration**: 2 weeks  
**Goal**: Build ML models for risk prediction.

### Tasks
- [ ] Explore historical risk data
- [ ] Engineer features (weather patterns, seasonality, POI density)
- [ ] Train initial ML models (sklearn, XGBoost)
- [ ] Implement model evaluation (cross-validation, metrics)
- [ ] Select best performing model
- [ ] Create `RiskPrediction` entity
- [ ] Create `RiskPredictionRepository`
- [ ] Create `PredictionService` to call trained model
- [ ] Expose prediction API: POST /api/predictions
- [ ] Implement batch predictions for all locations
- [ ] Add model versioning
- [ ] Write unit tests for prediction logic
- [ ] Write integration tests

### Testing
- Test prediction accuracy on holdout test set
- Test batch predictions (all locations)
- Test individual location predictions
- Test model versioning
- Load test predictions (high throughput)

### Learning
- ML pipeline design
- Feature engineering for time-series
- Model evaluation and selection
- Model serving and versioning
- Production ML systems

---

## Epic 14: Final Documentation and Demo

**Status**: PLANNED  
**Duration**: 1 week  
**Goal**: Complete documentation and prepare project for handoff.

### Tasks
- [ ] Update README with setup instructions
- [ ] Write API documentation (Swagger/OpenAPI)
- [ ] Write architecture decision records (ADRs)
- [ ] Create deployment guide (Docker, cloud platform)
- [ ] Write troubleshooting guide
- [ ] Create demo scenarios and scripts
- [ ] Record demo video
- [ ] Write contribution guidelines
- [ ] Set up CI/CD pipeline (GitHub Actions)
- [ ] Configure code coverage and quality gates
- [ ] Create performance benchmarks
- [ ] Prepare project for open source (if applicable)

### Testing
- Verify all documentation accuracy
- Test demo scenarios end-to-end
- Test CI/CD pipeline
- Verify code quality gates pass

### Learning
- API documentation best practices
- DevOps and CI/CD
- Open source project management
- Communication and documentation

---

## Timeline Summary

```
Week   1  │ Epic 0: Design & Documentation ✓
Week   2  │ Epic 1: Basic Backend
Week   3  │ Epic 2: Weather API
Week   4  │ Epic 3: Geocoding
Week   5  │ Epic 4: PostgreSQL & PostGIS
Week   6  │ Epic 5: Location & Weather CRUD
Week   7  │ Epic 6: OpenStreetMap POI
Week   8-9│ Epic 7: Risk Scoring
Week 10-11│ Epic 8: Frontend Map UI
Week  12  │ Epic 9: Scheduled Collection
Week 13-14│ Epic 10: Kafka Pipeline
Week 15-16│ Epic 11: AI Explanations
Week 17-18│ Epic 12: MCP Server
Week 19-20│ Epic 13: ML Prediction
Week 21-22│ Epic 14: Documentation & Demo
────────────────────────────────────
Total: ~5 months (22 weeks)
```

## Key Dependencies

- **Epics 1-7** are sequential and have hard dependencies
- **Epic 8** can start after Epic 7 is stable
- **Epic 9** requires Epic 7 (needs risk scoring)
- **Epic 10** depends on Epic 9 (migrates scheduler to Kafka)
- **Epic 11** depends on Epic 7 (needs risk assessments to explain)
- **Epic 12** depends on Epics 7+ (needs full API)
- **Epic 13** depends on Epic 7 (needs historical risk data)
- **Epic 14** is final polish (depends on all features)

## Validation Checkpoints

After each epic, validate:
1. ✓ Code compiles and runs
2. ✓ Tests pass (unit + integration)
3. ✓ No critical code quality issues
4. ✓ Documentation updated
5. ✓ API endpoints working
6. ✓ Performance acceptable
