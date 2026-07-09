# GeoRisk AI

A geospatial risk analysis platform that combines real-time weather data, OpenStreetMap points of interest, and geocoding services to assess and predict urban risk levels at specific geographic locations.

## Project Overview

GeoRisk AI is designed to provide intelligent, location-based risk assessment and prediction capabilities. The platform analyzes environmental and geographical factors to calculate risk scores and explain them using AI models. It supports scheduled data collection, real-time API integrations, and future machine learning-based risk predictions.

### Key Features (MVP)
- Location management with geocoding
- Real-time weather data integration
- Urban risk scoring based on environmental factors
- Risk assessment history and tracking
- RESTful API for all operations

### Future Capabilities
- AI-powered risk explanations
- Machine learning-based predictions
- Kafka data pipeline for high-volume collection
- MCP server integration for extended tools
- Scheduled location monitoring

## Tech Stack

### Backend
- **Language:** Java 17+
- **Framework:** Spring Boot (or Quarkus)
- **Database:** PostgreSQL with PostGIS extension
- **Build:** Maven
- **API:** REST/JSON

### Frontend (Planned)
- React or Vue.js
- Interactive map display
- Risk visualization

### AI & ML (Planned)
- Python (scikit-learn, TensorFlow)
- Jupyter notebooks for exploration

### Infrastructure (Planned)
- Docker & Docker Compose
- Kafka for event streaming
- MCP servers for extensibility

## Main Modules

### Backend Services
- **Health:** System health and readiness checks
- **Location:** Geographic location management and geocoding
- **Weather:** Real-time weather data collection and storage
- **POI:** Points of Interest detection and analysis
- **Risk:** Risk scoring and assessment
- **AI (Future):** Risk explanations and insights

### Additional Components
- **Frontend:** Web-based map interface
- **AI/ML:** Prediction and explanation models
- **MCP Server (Future):** Tool integration and extensibility
- **Kafka (Future):** High-throughput data pipeline

## MVP Scope

The MVP focuses on core functionality:
1. Backend REST API with location management
2. PostgreSQL database with locations, weather measurements, and risk assessments
3. Weather API integration
4. Basic urban risk scoring
5. API documentation

**Out of scope for MVP:** Frontend UI, AI explanations, Kafka pipeline, ML predictions, MCP servers

## Future Extensions

1. **Frontend Map Interface** - Interactive web UI for risk visualization
2. **AI Risk Explanations** - Machine learning models to explain why risk scores are assigned
3. **ML Predictions** - Predictive models for future risk levels
4. **Kafka Pipeline** - High-volume, low-latency data collection
5. **MCP Server** - Protocol integration for tool ecosystems
6. **Scheduled Collection** - Automated periodic data gathering
7. **Advanced Geocoding** - Multi-provider geocoding with fallback

## Current Status

**Phase:** Backend Foundation Setup
- [x] Architecture and design documentation
- [x] Database schema planning
- [x] Project roadmap definition
- [x] Development environment setup
- [x] Backend application scaffolding with Quarkus

## Project Structure

```
GeoRisk-AI/
├── backend/              # Java backend application
├── frontend/             # React/Vue.js frontend
├── ai/                   # Python AI/ML notebooks and scripts
├── mcp-server/           # MCP server implementation
├── docs/                 # Project documentation
├── docker/               # Docker configurations
├── README.md             # This file
└── .gitignore           # Git ignore rules
```

See [docs/project-structure.md](docs/project-structure.md) for detailed information about repository structure and backend package organization.

## Documentation

- [Project Structure](docs/project-structure.md) - Repository and code organization
- [Database Design](docs/database.md) - Data models and schema
- [Architecture](docs/architecture.md) - System design principles
- [Roadmap](docs/roadmap.md) - Development plan with epics and tasks

## Getting Started

(To be updated during development setup phase)

## License

(To be determined)

## Author

Sergiu