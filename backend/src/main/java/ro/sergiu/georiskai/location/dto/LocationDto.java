package ro.sergiu.georiskai.location.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LocationDto {
    public Long id;

    @NotBlank
    @Size(max = 255)
    public String name;

    @NotBlank
    @Size(max = 255)
    public String country;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    public BigDecimal latitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    public BigDecimal longitude;

    public LocalDateTime createdAt;

    public LocationDto() {
    }

    public LocationDto(Long id, String name, String country, BigDecimal latitude, BigDecimal longitude, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    public LocationDto(String name, String country, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationDto that = (LocationDto) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}
