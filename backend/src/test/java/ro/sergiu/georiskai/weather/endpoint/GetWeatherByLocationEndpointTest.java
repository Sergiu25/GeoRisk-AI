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
class GetWeatherByLocationEndpointTest {

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
    void shouldGetAllMeasurementsForLocation() {
        weatherService.recordMeasurement(new WeatherMeasurementDto(locationId, new BigDecimal("20.5"), 
                new BigDecimal("5.0"), new BigDecimal("10.0"), LocalDateTime.now()));
        weatherService.recordMeasurement(new WeatherMeasurementDto(locationId, new BigDecimal("18.3"), 
                new BigDecimal("3.2"), new BigDecimal("8.5"), LocalDateTime.now().minusHours(1)));
        
        List<WeatherMeasurementDto> measurements = given()
            .pathParam("locationId", locationId)
        .when()
            .get("/api/weather/location/{locationId}")
        .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .extract()
            .as(new TypeRef<List<WeatherMeasurementDto>>() {});
        
        assertEquals(2, measurements.size());
    }

    @Test
    void shouldReturnEmptyListForLocationWithoutMeasurements() {
        given()
            .pathParam("locationId", locationId)
        .when()
            .get("/api/weather/location/{locationId}")
        .then()
            .statusCode(200)
            .body("$", hasSize(0));
    }

    @Test
    void shouldFailForNonExistentLocation() {
        given()
            .pathParam("locationId", 9999)
        .when()
            .get("/api/weather/location/{locationId}")
        .then()
            .statusCode(404);
    }
}
