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
class GetRiskAssessmentsByLevelEndpointTest {

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
    void shouldGetAssessmentsByLevel() {
        riskService.createAssessment(new RiskAssessmentDto(locationId, new BigDecimal("25.0"), "low", 
                "[]", LocalDateTime.now()));
        riskService.createAssessment(new RiskAssessmentDto(locationId, new BigDecimal("75.0"), "high", 
                "[\"flood\"]", LocalDateTime.now().minusHours(1)));
        riskService.createAssessment(new RiskAssessmentDto(locationId, new BigDecimal("95.0"), "critical", 
                "[\"extreme\"]", LocalDateTime.now().minusHours(2)));
        
        List<RiskAssessmentDto> highRisks = given()
            .pathParam("level", "high")
        .when()
            .get("/api/risk-assessments/level/{level}")
        .then()
            .statusCode(200)
            .body("$", hasSize(1))
            .extract()
            .as(new TypeRef<List<RiskAssessmentDto>>() {});
    }

    @Test
    void shouldReturnEmptyListForUnknownLevel() {
        given()
            .pathParam("level", "unknown")
        .when()
            .get("/api/risk-assessments/level/{level}")
        .then()
            .statusCode(200)
            .body("$", hasSize(0));
    }
}
