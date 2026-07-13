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
class SearchLocationByCountryEndpointTest {

    @Inject
    LocationRepository locationRepository;

    @Inject
    LocationService locationService;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
    }

    @Test
    void shouldSearchLocationsByCountry() {
        locationService.createLocation(new LocationDto("Bucharest", "Romania", 
                new BigDecimal("44.43"), new BigDecimal("26.09")));
        locationService.createLocation(new LocationDto("Constanta", "Romania", 
                new BigDecimal("44.16"), new BigDecimal("28.65")));
        locationService.createLocation(new LocationDto("Sofia", "Bulgaria", 
                new BigDecimal("42.69"), new BigDecimal("23.32")));
        
        List<LocationDto> locations = given()
            .queryParam("country", "Romania")
        .when()
            .get("/api/locations/search/country")
        .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .extract()
            .as(new TypeRef<List<LocationDto>>() {});
        
        assertEquals(2, locations.size());
        assertTrue(locations.stream().allMatch(l -> "Romania".equals(l.country)));
    }

    @Test
    void shouldReturnEmptyListForNonExistentCountry() {
        given()
            .queryParam("country", "NonExistent")
        .when()
            .get("/api/locations/search/country")
        .then()
            .statusCode(200)
            .body("$", hasSize(0));
    }

    @Test
    void shouldFailWhenCountryParameterMissing() {
        given()
        .when()
            .get("/api/locations/search/country")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldFailWhenCountryParameterEmpty() {
        given()
            .queryParam("country", "")
        .when()
            .get("/api/locations/search/country")
        .then()
            .statusCode(400);
    }
}
