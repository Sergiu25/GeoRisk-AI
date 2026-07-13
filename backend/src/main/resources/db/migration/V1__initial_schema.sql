-- Create locations table
CREATE TABLE IF NOT EXISTS locations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(latitude, longitude)
);

CREATE INDEX idx_locations_country ON locations(country);
CREATE INDEX idx_locations_name ON locations(name);

-- Create weather_measurements table
CREATE TABLE IF NOT EXISTS weather_measurements (
    id BIGSERIAL PRIMARY KEY,
    location_id BIGINT NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    temperature DECIMAL(5, 2),
    precipitation DECIMAL(10, 2),
    wind_speed DECIMAL(10, 2),
    measured_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_weather_location_time ON weather_measurements(location_id, measured_at DESC);
CREATE INDEX idx_weather_measured_at ON weather_measurements(measured_at DESC);

-- Create risk_assessments table
CREATE TABLE IF NOT EXISTS risk_assessments (
    id BIGSERIAL PRIMARY KEY,
    location_id BIGINT NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    score DECIMAL(5, 2) NOT NULL CHECK (score >= 0 AND score <= 100),
    level VARCHAR(20) NOT NULL,
    reasons_json TEXT,
    assessed_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_risk_location_time ON risk_assessments(location_id, assessed_at DESC);
CREATE INDEX idx_risk_level ON risk_assessments(level);
