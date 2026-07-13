package ro.sergiu.georiskai.location.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.sergiu.georiskai.location.dto.LocationDto;
import ro.sergiu.georiskai.location.repository.LocationRepository;
import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class CreateLocationEndpointTest {

    @Inject
    LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
    }

    @Test
    void shouldCreateLocationSuccessfully() {
        LocationDto dto = new LocationDto("Bucharest", "Romania", 
                new BigDecimal("44.43"), new BigDecimal("26.09"));
        
        given()
            .contentType("application/json")
            .body(dto)
        .when()
            .post("/api/locations")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo("Bucharest"))
            .body("country", equalTo("Romania"))
            .body("latitude", notNullValue())
            .body("longitude", notNullValue())
            .body("createdAt", notNullValue());
    }

    @Test
    void shouldFailWhenNameIsNull() {
        LocationDto dto = new LocationDto(null, "Romania", new BigDecimal("44.43"), new BigDecimal("26.09"));
        
        given()
            .contentType("application/json")
            .body(dto)
        .when()
            .post("/api/locations")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldFailWhenCountryIsNull() {
        LocationDto dto = new LocationDto("Bucharest", null, new BigDecimal("44.43"), new BigDecimal("26.09"));
        
        given()
            .contentType("application/json")
            .body(dto)
        .when()
            .post("/api/locations")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldFailWhenCoordinatesAreNull() {
        LocationDto dto = new LocationDto("Bucharest", "Romania", null, new BigDecimal("26.09"));
        
        given()
            .contentType("application/json")
            .body(dto)
        .when()
            .post("/api/locations")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldCreateMultipleLocations() {
        LocationDto dto1 = new LocationDto("Bucharest", "Romania", new BigDecimal("44.43"), new BigDecimal("26.09"));
        LocationDto dto2 = new LocationDto("Sofia", "Bulgaria", new BigDecimal("42.69"), new BigDecimal("23.32"));
        
        given()
            .contentType("application/json")
            .body(dto1)
        .when()
            .post("/api/locations")
        .then()
            .statusCode(201);
        
        given()
            .contentType("application/json")
            .body(dto2)
        .when()
            .post("/api/locations")
        .then()
            .statusCode(201);
    }
}
