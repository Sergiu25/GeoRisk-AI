package ro.sergiu.georiskai.weather.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.sergiu.georiskai.location.dto.LocationDto;
import ro.sergiu.georiskai.location.service.LocationService;
import ro.sergiu.georiskai.weather.dto.WeatherMeasurementDto;
import ro.sergiu.georiskai.weather.repository.WeatherMeasurementRepository;
import ro.sergiu.georiskai.weather.service.WeatherMeasurementService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class GetRecentWeatherEndpointTest {

    @Inject
    LocationService locationService;

    @Inject
    WeatherMeasurementService weatherService;

    @Inject
    WeatherMeasurementRepository weatherRepository;

    private Long locationId;

    @BeforeEach
    void setUp() {
        weatherRepository.deleteAll();
        LocationDto location = locationService.createLocation(
                new LocationDto("Test City", "TestCountry", new BigDecimal("45.0"), new BigDecimal("25.0")));
        locationId = location.id;
    }

    @Test
    void shouldGetRecentMeasurements() {
        for (int i = 0; i < 15; i++) {
            weatherService.recordMeasurement(new WeatherMeasurementDto(locationId, new BigDecimal("20.0"), 
                    new BigDecimal("5.0"), new BigDecimal("10.0"), LocalDateTime.now().minusHours(i)));
        }
        
        List<WeatherMeasurementDto> recent = given()
            .pathParam("locationId", locationId)
            .queryParam("limit", 10)
        .when()
            .get("/api/weather/location/{locationId}/recent")
        .then()
            .statusCode(200)
            .body("$", hasSize(10))
            .extract()
            .as(new TypeRef<List<WeatherMeasurementDto>>() {});
        
        assertEquals(10, recent.size());
    }

    @Test
    void shouldRespectLimitParameter() {
        for (int i = 0; i < 20; i++) {
            weatherService.recordMeasurement(new WeatherMeasurementDto(locationId, new BigDecimal("20.0"), 
                    new BigDecimal("5.0"), new BigDecimal("10.0"), LocalDateTime.now().minusHours(i)));
        }
        
        List<WeatherMeasurementDto> recent = given()
            .pathParam("locationId", locationId)
            .queryParam("limit", 5)
        .when()
            .get("/api/weather/location/{locationId}/recent")
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<WeatherMeasurementDto>>() {});
        
        assertEquals(5, recent.size());
    }

    @Test
    void shouldUseDefaultLimit() {
        for (int i = 0; i < 20; i++) {
            weatherService.recordMeasurement(new WeatherMeasurementDto(locationId, new BigDecimal("20.0"), 
                    new BigDecimal("5.0"), new BigDecimal("10.0"), LocalDateTime.now().minusHours(i)));
        }
        
        List<WeatherMeasurementDto> recent = given()
            .pathParam("locationId", locationId)
        .when()
            .get("/api/weather/location/{locationId}/recent")
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<WeatherMeasurementDto>>() {});
        
        assertEquals(10, recent.size());
    }
}
