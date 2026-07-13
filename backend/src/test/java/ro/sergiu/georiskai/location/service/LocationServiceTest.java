package ro.sergiu.georiskai.location.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.sergiu.georiskai.common.exception.ApiException;
import ro.sergiu.georiskai.location.dto.LocationDto;
import ro.sergiu.georiskai.location.entity.Location;
import ro.sergiu.georiskai.location.repository.LocationRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LocationServiceTest {

    private LocationRepository repository;
    private LocationService service;

    @BeforeEach
    void setUp() {
        repository = mock(LocationRepository.class);
        service = new LocationService(repository);
    }

    @Test
    void shouldNormalizeAndCreateLocation() {
        when(repository.findByCoordinates(any(), any())).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            Location location = invocation.getArgument(0);
            location.id = 1L;
            location.createdAt = LocalDateTime.now();
            return null;
        }).when(repository).persist(any(Location.class));

        LocationDto created = service.createLocation(new LocationDto(
                "  Bucharest  ", "  Romania ", new BigDecimal("44.43"), new BigDecimal("26.09")));

        assertEquals(1L, created.id);
        assertEquals("Bucharest", created.name);
        assertEquals("Romania", created.country);
        verify(repository).persist(any(Location.class));
    }

    @Test
    void shouldRejectDuplicateCoordinates() {
        when(repository.findByCoordinates(any(), any())).thenReturn(Optional.of(new Location()));

        ApiException exception = assertThrows(ApiException.class, () -> service.createLocation(new LocationDto(
                "Bucharest", "Romania", new BigDecimal("44.43"), new BigDecimal("26.09"))));

        assertEquals(409, exception.status().getStatusCode());
    }
}
