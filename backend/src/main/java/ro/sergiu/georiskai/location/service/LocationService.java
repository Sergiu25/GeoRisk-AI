package ro.sergiu.georiskai.location.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ro.sergiu.georiskai.common.exception.ApiException;
import ro.sergiu.georiskai.location.dto.LocationDto;
import ro.sergiu.georiskai.location.entity.Location;
import ro.sergiu.georiskai.location.repository.LocationRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LocationService {

    private final LocationRepository locationRepository;

    @Inject
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Transactional
    public LocationDto createLocation(@NotNull @Valid LocationDto dto) {
        ensureCoordinatesAreAvailable(dto.latitude, dto.longitude, null);
        Location location = new Location(normalize(dto.name), normalize(dto.country), dto.latitude, dto.longitude);
        locationRepository.persist(location);
        return toDto(location);
    }

    public LocationDto getLocationById(Long id) {
        Location location = locationRepository.findByIdOptional(id).orElse(null);
        return location != null ? toDto(location) : null;
    }

    public List<LocationDto> getAllLocations() {
        return locationRepository.listAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<LocationDto> getLocationsByCountry(String country) {
        return locationRepository.findByCountry(country)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<LocationDto> searchLocationsByName(String name) {
        return locationRepository.findByName(name)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<LocationDto> getLocationByCoordinates(BigDecimal latitude, BigDecimal longitude) {
        return locationRepository.findByCoordinates(latitude, longitude)
                .map(this::toDto);
    }

    @Transactional
    public LocationDto updateLocation(Long id, @NotNull @Valid LocationDto dto) {
        Location location = locationRepository.findByIdOptional(id).orElse(null);
        if (location == null) {
            return null;
        }

        ensureCoordinatesAreAvailable(dto.latitude, dto.longitude, id);
        location.name = normalize(dto.name);
        location.country = normalize(dto.country);
        location.latitude = dto.latitude;
        location.longitude = dto.longitude;
        return toDto(location);
    }

    @Transactional
    public boolean deleteLocation(Long id) {
        return locationRepository.deleteById(id);
    }

    private LocationDto toDto(Location location) {
        return new LocationDto(location.id, location.name, location.country, location.latitude, location.longitude, location.createdAt);
    }

    private void ensureCoordinatesAreAvailable(BigDecimal latitude, BigDecimal longitude, Long excludedId) {
        boolean coordinatesExist = excludedId == null
                ? locationRepository.findByCoordinates(latitude, longitude).isPresent()
                : locationRepository.existsByCoordinatesExcludingId(latitude, longitude, excludedId);
        if (coordinatesExist) {
            throw ApiException.conflict("A location with these coordinates already exists");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
