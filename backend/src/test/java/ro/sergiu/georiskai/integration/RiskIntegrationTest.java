package ro.sergiu.georiskai.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.sergiu.georiskai.location.dto.LocationDto;
import ro.sergiu.georiskai.location.repository.LocationRepository;
import ro.sergiu.georiskai.location.service.LocationService;
import ro.sergiu.georiskai.risk.dto.RiskAssessmentDto;
import ro.sergiu.georiskai.risk.repository.RiskAssessmentRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class RiskIntegrationTest {

    @Inject
    LocationService locationService;

    @Inject
    LocationRepository locationRepository;

    @Inject
    RiskAssessmentRepository riskRepository;

    private Long locationId;

    @BeforeEach
    void setUp() {
        riskRepository.deleteAll();
        locationRepository.deleteAll();
        
        LocationDto location = locationService.createLocation(
                new LocationDto("Risk Test City", "TestCountry", new BigDecimal("45.0"), new BigDecimal("25.0")));
        locationId = location.id;
    }

    @Test
    void shouldCreateAndRetrieveRiskAssessments() {
        // Create assessments with different levels
        String[] levels = {"low", "medium", "high", "critical"};
        BigDecimal[] scores = {new BigDecimal("25"), new BigDecimal("50"), new BigDecimal("75"), new BigDecimal("95")};
        
        for (int i = 0; i < levels.length; i++) {
            RiskAssessmentDto dto = new RiskAssessmentDto(
                    locationId,
                    scores[i],
                    levels[i],
                    "[\"risk_factor_" + i + "\"]",
                    LocalDateTime.now().minusHours(i)
            );
            
            given()
                .contentType("application/json")
                .body(dto)
            .when()
                .post("/api/risk-assessments")
            .then()
                .statusCode(201);
        }
        
        // Retrieve all assessments
        List<RiskAssessmentDto> allAssessments = given()
            .pathParam("locationId", locationId)
        .when()
            .get("/api/risk-assessments/location/{locationId}")
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<RiskAssessmentDto>>() {});
        
        assertEquals(4, allAssessments.size());
    }

    @Test
    void shouldGetLatestAssessment() {
        // Create multiple assessments
        RiskAssessmentDto firstDto = new RiskAssessmentDto(
                locationId, new BigDecimal("25"), "low", "[]", LocalDateTime.now().minusHours(2)
        );
        RiskAssessmentDto latestDto = new RiskAssessmentDto(
                locationId, new BigDecimal("75"), "high", "[]", LocalDateTime.now()
        );
        
        given()
            .contentType("application/json")
            .body(firstDto)
        .when()
            .post("/api/risk-assessments")
        .then()
            .statusCode(201);
        
        given()
            .contentType("application/json")
            .body(latestDto)
        .when()
            .post("/api/risk-assessments")
        .then()
            .statusCode(201);
        
        // Get latest assessment
        RiskAssessmentDto latest = given()
            .pathParam("locationId", locationId)
        .when()
            .get("/api/risk-assessments/location/{locationId}/latest")
        .then()
            .statusCode(200)
            .extract()
            .as(RiskAssessmentDto.class);
        
        assertEquals("high", latest.level);
        assertEquals(0, new BigDecimal("75").compareTo(latest.score));
    }

    @Test
    void shouldGetCriticalRisksAcrossAllLocations() {
        // Create critical assessment
        RiskAssessmentDto criticalDto = new RiskAssessmentDto(
                locationId, new BigDecimal("95"), "critical", "[\"extreme_risk\"]", LocalDateTime.now()
        );
        
        given()
            .contentType("application/json")
            .body(criticalDto)
        .when()
            .post("/api/risk-assessments")
        .then()
            .statusCode(201);
        
        // Retrieve critical risks
        List<RiskAssessmentDto> critical = given()
        .when()
            .get("/api/risk-assessments/critical")
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<RiskAssessmentDto>>() {});
        
        assertEquals(1, critical.size());
        assertTrue(critical.stream().allMatch(r -> "critical".equals(r.level)));
    }
}
