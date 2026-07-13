package ro.sergiu.georiskai.risk.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.sergiu.georiskai.location.dto.LocationDto;
import ro.sergiu.georiskai.location.service.LocationService;
import ro.sergiu.georiskai.risk.dto.RiskAssessmentDto;
import ro.sergiu.georiskai.risk.repository.RiskAssessmentRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class CreateRiskAssessmentEndpointTest {

    @Inject
    LocationService locationService;

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
    void shouldCreateAssessmentSuccessfully() {
        RiskAssessmentDto dto = new RiskAssessmentDto(locationId, new BigDecimal("45.5"), "medium", 
                "[\"flood\", \"landslide\"]", LocalDateTime.now());
        
        given()
            .contentType("application/json")
            .body(dto)
        .when()
            .post("/api/risk-assessments")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("level", equalTo("medium"))
            .body("score", notNullValue());
    }

    @Test
    void shouldFailWhenScoreExceedsMax() {
        RiskAssessmentDto dto = new RiskAssessmentDto(locationId, new BigDecimal("150.0"), "high", 
                "[]", LocalDateTime.now());
        
        given()
            .contentType("application/json")
            .body(dto)
        .when()
            .post("/api/risk-assessments")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldFailWhenScoreIsNegative() {
        RiskAssessmentDto dto = new RiskAssessmentDto(locationId, new BigDecimal("-10.0"), "low", 
                "[]", LocalDateTime.now());
        
        given()
            .contentType("application/json")
            .body(dto)
        .when()
            .post("/api/risk-assessments")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldFailWhenLocationDoesNotExist() {
        RiskAssessmentDto dto = new RiskAssessmentDto(9999L, new BigDecimal("50.0"), "high", 
                "[]", LocalDateTime.now());
        
        given()
            .contentType("application/json")
            .body(dto)
        .when()
            .post("/api/risk-assessments")
        .then()
            .statusCode(400);
    }
}
