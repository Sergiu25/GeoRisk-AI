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
class UpdateLocationEndpointTest {

    @Inject
    LocationRepository locationRepository;

    @Inject
    LocationService locationService;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
    }

    @Test
    void shouldUpdateLocationSuccessfully() {
        LocationDto created = locationService.createLocation(
                new LocationDto("Old Name", "Romania", new BigDecimal("44.43"), new BigDecimal("26.09")));
        
        LocationDto updateDto = new LocationDto("New Name", "Romania", 
                new BigDecimal("44.43"), new BigDecimal("26.09"));
        
        given()
            .pathParam("id", created.id)
            .contentType("application/json")
            .body(updateDto)
        .when()
            .put("/api/locations/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(created.id.intValue()))
            .body("name", equalTo("New Name"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistent() {
        LocationDto updateDto = new LocationDto("New Name", "Romania", 
                new BigDecimal("44.43"), new BigDecimal("26.09"));
        
        given()
            .pathParam("id", 9999)
            .contentType("application/json")
            .body(updateDto)
        .when()
            .put("/api/locations/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    void shouldFailWhenNameIsNull() {
        LocationDto created = locationService.createLocation(
                new LocationDto("Original", "Romania", new BigDecimal("44.43"), new BigDecimal("26.09")));
        
        LocationDto updateDto = new LocationDto(null, "Romania", new BigDecimal("44.43"), new BigDecimal("26.09"));
        
        given()
            .pathParam("id", created.id)
            .contentType("application/json")
            .body(updateDto)
        .when()
            .put("/api/locations/{id}")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldUpdateCountryOnly() {
        LocationDto created = locationService.createLocation(
                new LocationDto("City", "OldCountry", new BigDecimal("44.43"), new BigDecimal("26.09")));
        
        LocationDto updateDto = new LocationDto("City", "NewCountry", 
                new BigDecimal("44.43"), new BigDecimal("26.09"));
        
        given()
            .pathParam("id", created.id)
            .contentType("application/json")
            .body(updateDto)
        .when()
            .put("/api/locations/{id}")
        .then()
            .statusCode(200)
            .body("country", equalTo("NewCountry"));
    }
}
