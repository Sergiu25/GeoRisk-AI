package ro.sergiu.georiskai.integration;

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
class LocationIntegrationTest {

    @Inject
    LocationRepository locationRepository;

    @Inject
    LocationService locationService;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
    }

    @Test
    void shouldCompleteLocationLifecycle() {
        // Create a location
        LocationDto createDto = new LocationDto("Integration Test City", "TestCountry", 
                new BigDecimal("45.0"), new BigDecimal("25.0"));
        
        LocationDto created = given()
            .contentType("application/json")
            .body(createDto)
        .when()
            .post("/api/locations")
        .then()
            .statusCode(201)
            .extract()
            .as(LocationDto.class);
        
        assertNotNull(created.id);
        assertEquals("Integration Test City", created.name);
        
        // Retrieve the location
        LocationDto retrieved = given()
            .pathParam("id", created.id)
        .when()
            .get("/api/locations/{id}")
        .then()
            .statusCode(200)
            .extract()
            .as(LocationDto.class);
        
        assertEquals(created.id, retrieved.id);
        
        // Update the location
        LocationDto updateDto = new LocationDto("Updated City", "TestCountry", 
                new BigDecimal("45.0"), new BigDecimal("25.0"));
        
        LocationDto updated = given()
            .pathParam("id", created.id)
            .contentType("application/json")
            .body(updateDto)
        .when()
            .put("/api/locations/{id}")
        .then()
            .statusCode(200)
            .extract()
            .as(LocationDto.class);
        
        assertEquals("Updated City", updated.name);
        
        // Verify update in database
        LocationDto verified = locationService.getLocationById(created.id);
        assertEquals("Updated City", verified.name);
        
        // Delete the location
        given()
            .pathParam("id", created.id)
        .when()
            .delete("/api/locations/{id}")
        .then()
            .statusCode(204);
        
        // Verify deletion
        given()
            .pathParam("id", created.id)
        .when()
            .get("/api/locations/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    void shouldHandleMultipleLocationsScenario() {
        // Create multiple locations
        for (int i = 1; i <= 3; i++) {
            LocationDto dto = new LocationDto("City " + i, "Country " + i, 
                    new BigDecimal("40." + i), new BigDecimal("20." + i));
            
            given()
                .contentType("application/json")
                .body(dto)
            .when()
                .post("/api/locations")
            .then()
                .statusCode(201);
        }
        
        // Retrieve all locations
        List<LocationDto> allLocations = given()
        .when()
            .get("/api/locations")
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<LocationDto>>() {});
        
        assertEquals(3, allLocations.size());
        
        // Search by country
        List<LocationDto> country1 = given()
            .queryParam("country", "Country 1")
        .when()
            .get("/api/locations/search/country")
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<LocationDto>>() {});
        
        assertEquals(1, country1.size());
        assertEquals("City 1", country1.get(0).name);
    }
}
