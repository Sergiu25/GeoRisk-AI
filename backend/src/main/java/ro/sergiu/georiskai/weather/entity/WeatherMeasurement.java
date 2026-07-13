package ro.sergiu.georiskai.weather.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import ro.sergiu.georiskai.location.entity.Location;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_measurements")
public class WeatherMeasurement extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    public Location location;

    @Column(precision = 5, scale = 2)
    public BigDecimal temperature;

    @Column(precision = 10, scale = 2)
    public BigDecimal precipitation;

    @Column(name = "wind_speed", precision = 10, scale = 2)
    public BigDecimal windSpeed;

    @Column(name = "measured_at", nullable = false)
    public LocalDateTime measuredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public WeatherMeasurement() {
    }

    public WeatherMeasurement(Location location, BigDecimal temperature, BigDecimal precipitation, BigDecimal windSpeed, LocalDateTime measuredAt) {
        this.location = location;
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.windSpeed = windSpeed;
        this.measuredAt = measuredAt;
    }
}
