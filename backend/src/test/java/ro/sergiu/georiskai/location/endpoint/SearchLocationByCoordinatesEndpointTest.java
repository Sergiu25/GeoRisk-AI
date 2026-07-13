package ro.sergiu.georiskai.location.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.sergiu.georiskai.location.dto.LocationDto;
import ro.sergiu.georiskai.location.repository.LocationRepository;
import ro.sergiu.georiskai.location.service.LocationService;
import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class SearchLocationByCoordinatesEndpointTest {

    @Inject
    LocationRepository locationRepository;

    @Inject
    LocationService locationService;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
    }

    @Test
    void shouldFindLocationByCoordinates() {
        locationService.createLocation(new LocationDto("Test City", "TestCountry", 
                new BigDecimal("40.12345"), new BigDecimal("20.54321")));
        
        given()
            .queryParam("lat", "40.12345")
            .queryParam("lon", "20.54321")
        .when()
            .get("/api/locations/search/coordinates")
        .then()
            .statusCode(200)
            .body("name", equalTo("Test City"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentCoordinates() {
        given()
            .queryParam("lat", "99.99999")
            .queryParam("lon", "99.99999")
        .when()
            .get("/api/locations/search/coordinates")
        .then()
            .statusCode(404);
    }

    @Test
    void shouldFailWhenLatitudeMissing() {
        given()
            .queryParam("lon", "20.54321")
        .when()
            .get("/api/locations/search/coordinates")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldFailWhenLongitudeMissing() {
        given()
            .queryParam("lat", "40.12345")
        .when()
            .get("/api/locations/search/coordinates")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldFailWhenBothCoordinatesMissing() {
        given()
        .when()
            .get("/api/locations/search/coordinates")
        .then()
            .statusCode(400);
    }
}
