package ro.sergiu.georiskai.risk.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.sergiu.georiskai.location.dto.LocationDto;
import ro.sergiu.georiskai.location.service.LocationService;
import ro.sergiu.georiskai.risk.dto.RiskAssessmentDto;
import ro.sergiu.georiskai.risk.repository.RiskAssessmentRepository;
import ro.sergiu.georiskai.risk.service.RiskAssessmentService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class GetRiskAssessmentsByLocationEndpointTest {

    @Inject
    LocationService locationService;

    @Inject
    RiskAssessmentService riskService;

    @Inject
    RiskAssessmentRepository riskRepository;

    private Long locationId;

    @BeforeEach
    void setUp() {
        riskRepository.deleteAll();
        LocationDto location = locationService.createLocation(
                new LocationDto("Risk Test City", "TestCountry", new BigDecimal("45.0"), new BigDecimal("25.0")));
        locationId = location.id;
    }

    @Test
    void shouldGetAllAssessmentsForLocation() {
        riskService.createAssessment(new RiskAssessmentDto(locationId, new BigDecimal("45.5"), "medium", 
                "[\"flood\"]", LocalDateTime.now()));
        riskService.createAssessment(new RiskAssessmentDto(locationId, new BigDecimal("75.0"), "high", 
                "[\"flood\", \"landslide\"]", LocalDateTime.now().minusHours(1)));
        
        List<RiskAssessmentDto> assessments = given()
            .pathParam("locationId", locationId)
        .when()
            .get("/api/risk-assessments/location/{locationId}")
        .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .extract()
            .as(new TypeRef<List<RiskAssessmentDto>>() {});
    }

    @Test
    void shouldReturnEmptyListForLocationWithoutAssessments() {
        given()
            .pathParam("locationId", locationId)
        .when()
            .get("/api/risk-assessments/location/{locationId}")
        .then()
            .statusCode(200)
            .body("$", hasSize(0));
    }

    @Test
    void shouldFailForNonExistentLocation() {
        given()
            .pathParam("locationId", 9999)
        .when()
            .get("/api/risk-assessments/location/{locationId}")
        .then()
            .statusCode(404);
    }
}
