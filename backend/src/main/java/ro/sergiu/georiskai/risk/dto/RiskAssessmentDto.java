package ro.sergiu.georiskai.risk.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RiskAssessmentDto {
    public Long id;

    @NotNull
    @Positive
    public Long locationId;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    public BigDecimal score;

    @NotBlank
    @Pattern(regexp = "low|medium|high|critical")
    public String level;

    public String reasonsJson;

    @NotNull
    public LocalDateTime assessedAt;

    public LocalDateTime createdAt;

    public RiskAssessmentDto() {
    }

    public RiskAssessmentDto(Long id, Long locationId, BigDecimal score, String level, String reasonsJson, LocalDateTime assessedAt, LocalDateTime createdAt) {
        this.id = id;
        this.locationId = locationId;
        this.score = score;
        this.level = level;
        this.reasonsJson = reasonsJson;
        this.assessedAt = assessedAt;
        this.createdAt = createdAt;
    }

    public RiskAssessmentDto(Long locationId, BigDecimal score, String level, String reasonsJson, LocalDateTime assessedAt) {
        this.locationId = locationId;
        this.score = score;
        this.level = level;
        this.reasonsJson = reasonsJson;
        this.assessedAt = assessedAt;
    }
}
