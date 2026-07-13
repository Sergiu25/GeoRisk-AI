package ro.sergiu.georiskai.risk.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import ro.sergiu.georiskai.location.entity.Location;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessment extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    public Location location;

    @Column(nullable = false, precision = 5, scale = 2)
    public BigDecimal score;

    @Column(nullable = false, length = 20)
    public String level;

    @Column(name = "reasons_json", columnDefinition = "TEXT")
    public String reasonsJson;

    @Column(name = "assessed_at", nullable = false)
    public LocalDateTime assessedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public RiskAssessment() {
    }

    public RiskAssessment(Location location, BigDecimal score, String level, String reasonsJson, LocalDateTime assessedAt) {
        this.location = location;
        this.score = score;
        this.level = level;
        this.reasonsJson = reasonsJson;
        this.assessedAt = assessedAt;
    }
}
