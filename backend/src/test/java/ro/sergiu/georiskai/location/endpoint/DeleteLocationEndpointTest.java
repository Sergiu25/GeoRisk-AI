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
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class DeleteLocationEndpointTest {

    @Inject
    LocationRepository locationRepository;

    @Inject
    LocationService locationService;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
    }

    @Test
    void shouldDeleteLocationSuccessfully() {
        LocationDto created = locationService.createLocation(
                new LocationDto("To Delete", "Romania", new BigDecimal("44.43"), new BigDecimal("26.09")));
        
        given()
            .pathParam("id", created.id)
        .when()
            .delete("/api/locations/{id}")
        .then()
            .statusCode(204);
        
        given()
            .pathParam("id", created.id)
        .when()
            .get("/api/locations/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistent() {
        given()
            .pathParam("id", 9999)
        .when()
            .delete("/api/locations/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    void shouldNotBeAbleToGetDeletedLocation() {
        LocationDto created = locationService.createLocation(
                new LocationDto("Test", "Romania", new BigDecimal("44.43"), new BigDecimal("26.09")));
        Long id = created.id;
        
        given()
            .pathParam("id", id)
        .when()
            .delete("/api/locations/{id}")
        .then()
            .statusCode(204);
        
        LocationDto notFound = locationService.getLocationById(id);
        assertNull(notFound);
    }

    @Test
    void shouldDeleteMultipleLocations() {
        LocationDto loc1 = locationService.createLocation(
                new LocationDto("Loc1", "Romania", new BigDecimal("44.43"), new BigDecimal("26.09")));
        LocationDto loc2 = locationService.createLocation(
                new LocationDto("Loc2", "Romania", new BigDecimal("45.00"), new BigDecimal("25.00")));
        
        given()
            .pathParam("id", loc1.id)
        .when()
            .delete("/api/locations/{id}")
        .then()
            .statusCode(204);
        
        given()
            .pathParam("id", loc2.id)
        .when()
            .delete("/api/locations/{id}")
        .then()
            .statusCode(204);
    }
}
