package ro.sergiu.georiskai.weather.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ro.sergiu.georiskai.common.exception.ApiException;
import ro.sergiu.georiskai.location.entity.Location;
import ro.sergiu.georiskai.location.repository.LocationRepository;
import ro.sergiu.georiskai.weather.dto.WeatherMeasurementDto;
import ro.sergiu.georiskai.weather.entity.WeatherMeasurement;
import ro.sergiu.georiskai.weather.repository.WeatherMeasurementRepository;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class WeatherMeasurementService {

    private final WeatherMeasurementRepository weatherRepository;
    private final LocationRepository locationRepository;

    @Inject
    public WeatherMeasurementService(WeatherMeasurementRepository weatherRepository, LocationRepository locationRepository) {
        this.weatherRepository = weatherRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public WeatherMeasurementDto recordMeasurement(@NotNull @Valid WeatherMeasurementDto dto) {
        Location location = locationRepository.findByIdOptional(dto.locationId)
                .orElseThrow(() -> ApiException.badRequest("Location not found"));
        WeatherMeasurement measurement = new WeatherMeasurement(location, dto.temperature, dto.precipitation, dto.windSpeed, dto.measuredAt);
        weatherRepository.persist(measurement);
        return toDto(measurement);
    }

    public WeatherMeasurementDto getMeasurementById(Long id) {
        WeatherMeasurement measurement = weatherRepository.findByIdOptional(id).orElse(null);
        return measurement != null ? toDto(measurement) : null;
    }

    public List<WeatherMeasurementDto> getMeasurementsByLocation(Long locationId) {
        Location location = requireLocation(locationId);
        return weatherRepository.findByLocation(location)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<WeatherMeasurementDto> getMeasurementsByLocationAndDateRange(Long locationId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw ApiException.badRequest("Start date must be before or equal to end date");
        }
        Location location = requireLocation(locationId);
        return weatherRepository.findByLocationAndDateRange(location, startDate, endDate)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<WeatherMeasurementDto> getRecentMeasurements(Long locationId, int limit) {
        if (limit < 1 || limit > 100) {
            throw ApiException.badRequest("Limit must be between 1 and 100");
        }
        Location location = requireLocation(locationId);
        return weatherRepository.findRecentByLocation(location, limit)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public boolean deleteMeasurement(Long id) {
        return weatherRepository.deleteById(id);
    }

    private WeatherMeasurementDto toDto(WeatherMeasurement measurement) {
        return new WeatherMeasurementDto(measurement.id, measurement.location.id, measurement.temperature, 
                                        measurement.precipitation, measurement.windSpeed, measurement.measuredAt, measurement.createdAt);
    }

    private Location requireLocation(Long locationId) {
        return locationRepository.findByIdOptional(locationId)
                .orElseThrow(() -> ApiException.notFound("Location not found"));
    }
}
