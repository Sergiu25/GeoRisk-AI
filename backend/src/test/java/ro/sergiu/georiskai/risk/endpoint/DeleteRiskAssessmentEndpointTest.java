package ro.sergiu.georiskai.risk.endpoint;

import io.quarkus.test.junit.QuarkusTest;
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

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class DeleteRiskAssessmentEndpointTest {

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
    void shouldDeleteAssessmentSuccessfully() {
        RiskAssessmentDto created = riskService.createAssessment(
                new RiskAssessmentDto(locationId, new BigDecimal("50.0"), "medium", 
                "[]", LocalDateTime.now()));
        
        given()
            .pathParam("id", created.id)
        .when()
            .delete("/api/risk-assessments/{id}")
        .then()
            .statusCode(204);
        
        assertNull(riskService.getAssessmentById(created.id));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistent() {
        given()
            .pathParam("id", 9999)
        .when()
            .delete("/api/risk-assessments/{id}")
        .then()
            .statusCode(404);
    }
}
