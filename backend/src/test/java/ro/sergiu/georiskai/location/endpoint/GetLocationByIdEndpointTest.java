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
class GetLocationByIdEndpointTest {

    @Inject
    LocationRepository locationRepository;

    @Inject
    LocationService locationService;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
    }

    @Test
    void shouldGetLocationById() {
        LocationDto created = locationService.createLocation(
                new LocationDto("Sofia", "Bulgaria", new BigDecimal("42.69"), new BigDecimal("23.32")));
        
        given()
            .pathParam("id", created.id)
        .when()
            .get("/api/locations/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(created.id.intValue()))
            .body("name", equalTo("Sofia"))
            .body("country", equalTo("Bulgaria"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentId() {
        given()
            .pathParam("id", 9999)
        .when()
            .get("/api/locations/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    void shouldReturnCompleteLocationData() {
        LocationDto created = locationService.createLocation(
                new LocationDto("Test City", "TestCountry", new BigDecimal("45.12"), new BigDecimal("25.34")));
        
        given()
            .pathParam("id", created.id)
        .when()
            .get("/api/locations/{id}")
        .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("name", notNullValue())
            .body("country", notNullValue())
            .body("latitude", notNullValue())
            .body("longitude", notNullValue())
            .body("createdAt", notNullValue());
    }

    @Test
    void shouldReturnZeroForInvalidId() {
        given()
            .pathParam("id", 0)
        .when()
            .get("/api/locations/{id}")
        .then()
            .statusCode(404);
    }
}
