package ro.sergiu.georiskai.risk.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.sergiu.georiskai.common.exception.ApiException;
import ro.sergiu.georiskai.location.entity.Location;
import ro.sergiu.georiskai.location.repository.LocationRepository;
import ro.sergiu.georiskai.risk.dto.RiskAssessmentDto;
import ro.sergiu.georiskai.risk.repository.RiskAssessmentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RiskAssessmentServiceTest {

    private LocationRepository locationRepository;
    private RiskAssessmentService service;

    @BeforeEach
    void setUp() {
        locationRepository = mock(LocationRepository.class);
        service = new RiskAssessmentService(mock(RiskAssessmentRepository.class), locationRepository);
    }

    @Test
    void shouldRejectScoreOutsideAcceptedRange() {
        Location location = new Location();
        location.id = 1L;
        when(locationRepository.findByIdOptional(1L)).thenReturn(Optional.of(location));
        RiskAssessmentDto dto = new RiskAssessmentDto(
                1L, new BigDecimal("100.01"), "critical", "[]", LocalDateTime.now());

        ApiException exception = assertThrows(ApiException.class, () -> service.createAssessment(dto));

        assertEquals(400, exception.status().getStatusCode());
    }

    @Test
    void shouldReturnNotFoundForUnknownLocationHistory() {
        when(locationRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> service.getAssessmentsByLocation(99L));

        assertEquals(404, exception.status().getStatusCode());
    }
}
