package ro.sergiu.georiskai.location.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.sergiu.georiskai.location.dto.LocationDto;
import ro.sergiu.georiskai.location.repository.LocationRepository;
import ro.sergiu.georiskai.location.service.LocationService;
import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class GetAllLocationsEndpointTest {

    @Inject
    LocationRepository locationRepository;

    @Inject
    LocationService locationService;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoLocations() {
        given()
        .when()
            .get("/api/locations")
        .then()
            .statusCode(200)
            .body("$", hasSize(0));
    }

    @Test
    void shouldReturnAllLocations() {
        locationService.createLocation(new LocationDto("Bucharest", "Romania", 
                new BigDecimal("44.43"), new BigDecimal("26.09")));
        locationService.createLocation(new LocationDto("Sofia", "Bulgaria", 
                new BigDecimal("42.69"), new BigDecimal("23.32")));
        locationService.createLocation(new LocationDto("Athens", "Greece", 
                new BigDecimal("37.98"), new BigDecimal("23.73")));
        
        List<LocationDto> locations = given()
        .when()
            .get("/api/locations")
        .then()
            .statusCode(200)
            .body("$", hasSize(3))
            .extract()
            .as(new TypeRef<List<LocationDto>>() {});
        
        assertEquals(3, locations.size());
    }

    @Test
    void shouldReturnLocationsWithCorrectData() {
        locationService.createLocation(new LocationDto("Bucharest", "Romania", 
                new BigDecimal("44.43"), new BigDecimal("26.09")));
        
        List<LocationDto> locations = given()
        .when()
            .get("/api/locations")
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<LocationDto>>() {});
        
        assertTrue(locations.stream().anyMatch(l -> "Bucharest".equals(l.name)));
        assertTrue(locations.stream().anyMatch(l -> "Romania".equals(l.country)));
    }

    @Test
    void shouldReturnLocationsWithTimestamps() {
        locationService.createLocation(new LocationDto("Bucharest", "Romania", 
                new BigDecimal("44.43"), new BigDecimal("26.09")));
        
        given()
        .when()
            .get("/api/locations")
        .then()
            .statusCode(200)
            .body("[0].createdAt", notNullValue())
            .body("[0].id", notNullValue());
    }
}
