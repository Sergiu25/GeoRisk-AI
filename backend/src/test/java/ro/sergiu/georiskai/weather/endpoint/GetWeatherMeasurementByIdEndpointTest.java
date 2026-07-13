package ro.sergiu.georiskai.weather.endpoint;

import io.quarkus.test.junit.QuarkusTest;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class GetWeatherMeasurementByIdEndpointTest {

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
    void shouldGetMeasurementById() {
        WeatherMeasurementDto recorded = weatherService.recordMeasurement(
                new WeatherMeasurementDto(locationId, new BigDecimal("20.5"), new BigDecimal("5.0"), 
                new BigDecimal("10.0"), LocalDateTime.now()));
        
        given()
            .pathParam("id", recorded.id)
        .when()
            .get("/api/weather/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(recorded.id.intValue()))
            .body("locationId", equalTo(locationId.intValue()));
    }

    @Test
    void shouldReturnNotFoundForNonExistentId() {
        given()
            .pathParam("id", 9999)
        .when()
            .get("/api/weather/{id}")
        .then()
            .statusCode(404);
    }
}
