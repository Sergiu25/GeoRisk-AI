package ro.sergiu.georiskai.weather.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WeatherMeasurementDto {
    public Long id;

    @NotNull
    @Positive
    public Long locationId;

    public BigDecimal temperature;

    @DecimalMin("0.0")
    public BigDecimal precipitation;

    @DecimalMin("0.0")
    public BigDecimal windSpeed;

    @NotNull
    public LocalDateTime measuredAt;

    public LocalDateTime createdAt;

    public WeatherMeasurementDto() {
    }

    public WeatherMeasurementDto(Long id, Long locationId, BigDecimal temperature, BigDecimal precipitation, 
                                 BigDecimal windSpeed, LocalDateTime measuredAt, LocalDateTime createdAt) {
        this.id = id;
        this.locationId = locationId;
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.windSpeed = windSpeed;
        this.measuredAt = measuredAt;
        this.createdAt = createdAt;
    }

    public WeatherMeasurementDto(Long locationId, BigDecimal temperature, BigDecimal precipitation, 
                                 BigDecimal windSpeed, LocalDateTime measuredAt) {
        this.locationId = locationId;
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.windSpeed = windSpeed;
        this.measuredAt = measuredAt;
    }
}
