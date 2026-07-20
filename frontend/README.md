# GeoRisk AI Frontend

Initial visual MVP for GeoRisk AI, built with Angular, TypeScript, SCSS and Leaflet.

## Prerequisites

- Node.js 24 LTS (Angular 22 also supports compatible Node.js 22 releases)
- npm 11+

## Install dependencies

From the repository root:

```powershell
cd frontend
npm install
```

If PowerShell blocks `npm.ps1`, use the Windows command shim:

```powershell
npm.cmd install
```

## Run the frontend

```powershell
cd frontend
npm start
```

Then open:

```text
http://localhost:4200
```

On PowerShell systems that block scripts:

```powershell
npm.cmd start
```

## Build and test

```powershell
npm run build
npm test -- --watch=false
```

The production output is generated in `dist/georisk-frontend/`.

## What is currently mocked

- Ten risk cells around Cluj-Napoca
- Risk scores and LOW, MODERATE or HIGH levels
- Assessment summaries and contributing factors
- Nearby city search options

The map uses OpenStreetMap tiles. Map tiles require internet access in the browser, while the application build itself works offline after dependencies are installed.

## What will be connected later

The placeholder services under `src/app/core/api/` define the future integration boundary for:

- locations from the Quarkus backend
- weather measurements
- risk assessments

The current UI does not call those services. AI explanations, Kafka, MCP and database logic are intentionally outside this frontend MVP.

## Structure

```text
src/app/
├── core/
│   ├── api/                 # Future backend clients
│   └── models/              # Shared domain contracts
├── features/
│   ├── map/                 # Leaflet map, mock cells, legend and details
│   └── search/              # City search
└── shared/                  # Cross-feature presentation constants
```
