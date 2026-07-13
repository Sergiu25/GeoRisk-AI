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
class SearchLocationByNameEndpointTest {

    @Inject
    LocationRepository locationRepository;

    @Inject
    LocationService locationService;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
    }

    @Test
    void shouldSearchLocationsByName() {
        locationService.createLocation(new LocationDto("Bucharest", "Romania", 
                new BigDecimal("44.43"), new BigDecimal("26.09")));
        locationService.createLocation(new LocationDto("Constanta", "Romania", 
                new BigDecimal("44.16"), new BigDecimal("28.65")));
        locationService.createLocation(new LocationDto("Sofia", "Bulgaria", 
                new BigDecimal("42.69"), new BigDecimal("23.32")));
        
        List<LocationDto> locations = given()
            .queryParam("q", "Con")
        .when()
            .get("/api/locations/search/name")
        .then()
            .statusCode(200)
            .body("$", hasSize(1))
            .extract()
            .as(new TypeRef<List<LocationDto>>() {});
        
        assertEquals(1, locations.size());
        assertEquals("Constanta", locations.get(0).name);
    }

    @Test
    void shouldSearchCaseInsensitive() {
        locationService.createLocation(new LocationDto("Bucharest", "Romania", 
                new BigDecimal("44.43"), new BigDecimal("26.09")));
        
        List<LocationDto> locations = given()
            .queryParam("q", "BUCHAREST")
        .when()
            .get("/api/locations/search/name")
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<LocationDto>>() {});
        
        assertEquals(1, locations.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoMatch() {
        locationService.createLocation(new LocationDto("Bucharest", "Romania", 
                new BigDecimal("44.43"), new BigDecimal("26.09")));
        
        given()
            .queryParam("q", "NonExistent")
        .when()
            .get("/api/locations/search/name")
        .then()
            .statusCode(200)
            .body("$", hasSize(0));
    }

    @Test
    void shouldFailWhenQueryParameterMissing() {
        given()
        .when()
            .get("/api/locations/search/name")
        .then()
            .statusCode(400);
    }
}
