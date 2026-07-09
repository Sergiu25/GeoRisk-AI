# GeoRisk AI Backend

This module contains the initial Quarkus backend skeleton for GeoRisk AI.

## Run locally in dev mode

From the repository root:

```bash
cd backend
mvn quarkus:dev
```

If you are on Windows PowerShell, use the local wrapper:

```powershell
cd backend
.\mvn-local.cmd quarkus:dev
```

## Test the health endpoint

```bash
curl http://localhost:8080/health
```

Expected response:

```json
{
  "status": "UP",
  "service": "GeoRisk AI Backend"
}
```

## Swagger / OpenAPI

When running in dev mode, the API documentation is available at:

- Swagger UI: http://localhost:8080/q/swagger-ui
- OpenAPI JSON: http://localhost:8080/q/openapi

## Package structure

The backend is organized around feature-based modules:

- `common/` for shared configuration, exceptions, and validation
- `health/` for health endpoints
- `location/` for future location services and DTOs
- `weather/` for future weather integration and DTOs
- `poi/` for future POI-related DTOs
- `risk/` for future risk scoring DTOs
- `ai/` for future AI explanation DTOs
