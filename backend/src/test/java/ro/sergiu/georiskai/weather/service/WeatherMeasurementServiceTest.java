package ro.sergiu.georiskai.weather.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.sergiu.georiskai.common.exception.ApiException;
import ro.sergiu.georiskai.location.repository.LocationRepository;
import ro.sergiu.georiskai.weather.dto.WeatherMeasurementDto;
import ro.sergiu.georiskai.weather.repository.WeatherMeasurementRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WeatherMeasurementServiceTest {

    private LocationRepository locationRepository;
    private WeatherMeasurementService service;

    @BeforeEach
    void setUp() {
        locationRepository = mock(LocationRepository.class);
        service = new WeatherMeasurementService(mock(WeatherMeasurementRepository.class), locationRepository);
    }

    @Test
    void shouldRejectMeasurementForMissingLocation() {
        when(locationRepository.findByIdOptional(99L)).thenReturn(Optional.empty());
        WeatherMeasurementDto dto = new WeatherMeasurementDto(
                99L, new BigDecimal("20"), BigDecimal.ZERO, BigDecimal.ONE, LocalDateTime.now());

        ApiException exception = assertThrows(ApiException.class, () -> service.recordMeasurement(dto));

        assertEquals(400, exception.status().getStatusCode());
    }

    @Test
    void shouldRejectInvalidRecentMeasurementsLimit() {
        ApiException exception = assertThrows(ApiException.class, () -> service.getRecentMeasurements(1L, 0));

        assertEquals(400, exception.status().getStatusCode());
    }
}
