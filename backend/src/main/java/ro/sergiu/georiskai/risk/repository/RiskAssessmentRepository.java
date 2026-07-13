package ro.sergiu.georiskai.risk.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ro.sergiu.georiskai.risk.entity.RiskAssessment;
import ro.sergiu.georiskai.location.entity.Location;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RiskAssessmentRepository implements PanacheRepository<RiskAssessment> {

    @Override
    @Transactional
    public long deleteAll() {
        return getEntityManager()
                .createQuery("delete from RiskAssessment")
                .executeUpdate();
    }

    public List<RiskAssessment> findByLocation(Location location) {
        return find("location = ?1 ORDER BY assessedAt DESC", location).list();
    }

    public Optional<RiskAssessment> findLatestByLocation(Location location) {
        return find("location = ?1 ORDER BY assessedAt DESC", location)
                .firstResultOptional();
    }

    public List<RiskAssessment> findByLocationAndDateRange(Location location, LocalDateTime startDate, LocalDateTime endDate) {
        return find("location = ?1 AND assessedAt BETWEEN ?2 AND ?3 ORDER BY assessedAt DESC", location, startDate, endDate).list();
    }

    public List<RiskAssessment> findByLevel(String level) {
        return find("level = ?1 ORDER BY assessedAt DESC", level).list();
    }

    public List<RiskAssessment> findCriticalRisks() {
        return findByLevel("critical");
    }
}
