package ro.sergiu.georiskai.risk.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ro.sergiu.georiskai.common.exception.ApiException;
import ro.sergiu.georiskai.location.entity.Location;
import ro.sergiu.georiskai.location.repository.LocationRepository;
import ro.sergiu.georiskai.risk.dto.RiskAssessmentDto;
import ro.sergiu.georiskai.risk.entity.RiskAssessment;
import ro.sergiu.georiskai.risk.repository.RiskAssessmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RiskAssessmentService {

    private final RiskAssessmentRepository riskRepository;
    private final LocationRepository locationRepository;

    @Inject
    public RiskAssessmentService(RiskAssessmentRepository riskRepository, LocationRepository locationRepository) {
        this.riskRepository = riskRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public RiskAssessmentDto createAssessment(@NotNull @Valid RiskAssessmentDto dto) {
        Location location = locationRepository.findByIdOptional(dto.locationId)
                .orElseThrow(() -> ApiException.badRequest("Location not found"));
        if (dto.score == null || dto.score.compareTo(java.math.BigDecimal.ZERO) < 0 || dto.score.compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
            throw ApiException.badRequest("Score must be between 0 and 100");
        }
        RiskAssessment assessment = new RiskAssessment(location, dto.score, dto.level, dto.reasonsJson, dto.assessedAt);
        riskRepository.persist(assessment);
        return toDto(assessment);
    }

    public RiskAssessmentDto getAssessmentById(Long id) {
        RiskAssessment assessment = riskRepository.findByIdOptional(id).orElse(null);
        return assessment != null ? toDto(assessment) : null;
    }

    public List<RiskAssessmentDto> getAssessmentsByLocation(Long locationId) {
        Location location = requireLocation(locationId);
        return riskRepository.findByLocation(location)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<RiskAssessmentDto> getLatestAssessmentByLocation(Long locationId) {
        Location location = requireLocation(locationId);
        return riskRepository.findLatestByLocation(location)
                .map(this::toDto);
    }

    public List<RiskAssessmentDto> getAssessmentsByLocationAndDateRange(Long locationId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw ApiException.badRequest("Start date must be before or equal to end date");
        }
        Location location = requireLocation(locationId);
        return riskRepository.findByLocationAndDateRange(location, startDate, endDate)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<RiskAssessmentDto> getAssessmentsByLevel(String level) {
        return riskRepository.findByLevel(level)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<RiskAssessmentDto> getCriticalRisks() {
        return riskRepository.findCriticalRisks()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public boolean deleteAssessment(Long id) {
        return riskRepository.deleteById(id);
    }

    private RiskAssessmentDto toDto(RiskAssessment assessment) {
        return new RiskAssessmentDto(assessment.id, assessment.location.id, assessment.score, assessment.level, 
                                     assessment.reasonsJson, assessment.assessedAt, assessment.createdAt);
    }

    private Location requireLocation(Long locationId) {
        return locationRepository.findByIdOptional(locationId)
                .orElseThrow(() -> ApiException.notFound("Location not found"));
    }
}
