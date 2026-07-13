package ro.sergiu.georiskai.location.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "locations")
public class Location extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 255)
    public String name;

    @Column(nullable = false, length = 255)
    public String country;

    @Column(nullable = false, precision = 10, scale = 8)
    public BigDecimal latitude;

    @Column(nullable = false, precision = 11, scale = 8)
    public BigDecimal longitude;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Location() {
    }

    public Location(String name, String country, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
