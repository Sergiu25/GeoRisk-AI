package ro.sergiu.georiskai.weather.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.sergiu.georiskai.location.dto.LocationDto;
import ro.sergiu.georiskai.location.service.LocationService;
import ro.sergiu.georiskai.weather.dto.WeatherMeasurementDto;
import ro.sergiu.georiskai.weather.repository.WeatherMeasurementRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class RecordWeatherMeasurementEndpointTest {

    @Inject
    LocationService locationService;

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
    void shouldRecordMeasurementSuccessfully() {
        WeatherMeasurementDto dto = new WeatherMeasurementDto(locationId, new BigDecimal("20.5"), 
                new BigDecimal("5.0"), new BigDecimal("10.0"), LocalDateTime.now());
        
        given()
            .contentType("application/json")
            .body(dto)
        .when()
            .post("/api/weather")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("locationId", equalTo(locationId.intValue()))
            .body("temperature", notNullValue());
    }

    @Test
    void shouldFailWhenLocationDoesNotExist() {
        WeatherMeasurementDto dto = new WeatherMeasurementDto(9999L, new BigDecimal("20.5"), 
                new BigDecimal("5.0"), new BigDecimal("10.0"), LocalDateTime.now());
        
        given()
            .contentType("application/json")
            .body(dto)
        .when()
            .post("/api/weather")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldFailWhenLocationIdIsNull() {
        WeatherMeasurementDto dto = new WeatherMeasurementDto(null, new BigDecimal("20.5"), 
                new BigDecimal("5.0"), new BigDecimal("10.0"), LocalDateTime.now());
        
        given()
            .contentType("application/json")
            .body(dto)
        .when()
            .post("/api/weather")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldRecordWithNullTemperature() {
        WeatherMeasurementDto dto = new WeatherMeasurementDto(locationId, null, 
                new BigDecimal("5.0"), new BigDecimal("10.0"), LocalDateTime.now());
        
        given()
            .contentType("application/json")
            .body(dto)
        .when()
            .post("/api/weather")
        .then()
            .statusCode(201);
    }
}
