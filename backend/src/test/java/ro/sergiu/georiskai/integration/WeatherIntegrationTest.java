package ro.sergiu.georiskai.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.sergiu.georiskai.location.dto.LocationDto;
import ro.sergiu.georiskai.location.repository.LocationRepository;
import ro.sergiu.georiskai.location.service.LocationService;
import ro.sergiu.georiskai.weather.dto.WeatherMeasurementDto;
import ro.sergiu.georiskai.weather.repository.WeatherMeasurementRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class WeatherIntegrationTest {

    @Inject
    LocationService locationService;

    @Inject
    LocationRepository locationRepository;

    @Inject
    WeatherMeasurementRepository weatherRepository;

    private Long locationId;

    @BeforeEach
    void setUp() {
        weatherRepository.deleteAll();
        locationRepository.deleteAll();
        
        LocationDto location = locationService.createLocation(
                new LocationDto("Weather Test City", "TestCountry", new BigDecimal("45.0"), new BigDecimal("25.0")));
        locationId = location.id;
    }

    @Test
    void shouldRecordAndRetrieveWeatherMeasurements() {
        // Record multiple measurements
        for (int i = 0; i < 5; i++) {
            WeatherMeasurementDto dto = new WeatherMeasurementDto(
                    locationId, 
                    new BigDecimal("20." + i), 
                    new BigDecimal("5." + i), 
                    new BigDecimal("10." + i), 
                    LocalDateTime.now().minusHours(i)
            );
            
            given()
                .contentType("application/json")
                .body(dto)
            .when()
                .post("/api/weather")
            .then()
                .statusCode(201);
        }
        
        // Retrieve all measurements for location
        List<WeatherMeasurementDto> measurements = given()
            .pathParam("locationId", locationId)
        .when()
            .get("/api/weather/location/{locationId}")
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<WeatherMeasurementDto>>() {});
        
        assertEquals(5, measurements.size());
    }

    @Test
    void shouldGetRecentMeasurementsWithLimit() {
        // Record 15 measurements
        for (int i = 0; i < 15; i++) {
            WeatherMeasurementDto dto = new WeatherMeasurementDto(
                    locationId, 
                    new BigDecimal("20.0"), 
                    new BigDecimal("5.0"), 
                    new BigDecimal("10.0"), 
                    LocalDateTime.now().minusHours(i)
            );
            
            given()
                .contentType("application/json")
                .body(dto)
            .when()
                .post("/api/weather")
            .then()
                .statusCode(201);
        }
        
        // Get recent with default limit
        List<WeatherMeasurementDto> recent = given()
            .pathParam("locationId", locationId)
        .when()
            .get("/api/weather/location/{locationId}/recent")
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<WeatherMeasurementDto>>() {});
        
        assertEquals(10, recent.size());
        
        // Get recent with custom limit
        List<WeatherMeasurementDto> limited = given()
            .pathParam("locationId", locationId)
            .queryParam("limit", 5)
        .when()
            .get("/api/weather/location/{locationId}/recent")
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<WeatherMeasurementDto>>() {});
        
        assertEquals(5, limited.size());
    }
}
