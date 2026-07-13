package ro.sergiu.georiskai.weather.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ro.sergiu.georiskai.weather.entity.WeatherMeasurement;
import ro.sergiu.georiskai.location.entity.Location;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class WeatherMeasurementRepository implements PanacheRepository<WeatherMeasurement> {

    @Override
    @Transactional
    public long deleteAll() {
        return getEntityManager()
                .createQuery("delete from WeatherMeasurement")
                .executeUpdate();
    }

    public List<WeatherMeasurement> findByLocation(Location location) {
        return find("location = ?1 ORDER BY measuredAt DESC", location).list();
    }

    public List<WeatherMeasurement> findByLocationAndDateRange(Location location, LocalDateTime startDate, LocalDateTime endDate) {
        return find("location = ?1 AND measuredAt BETWEEN ?2 AND ?3 ORDER BY measuredAt DESC", location, startDate, endDate).list();
    }

    public List<WeatherMeasurement> findRecentByLocation(Location location, int limit) {
        return find("location = ?1 ORDER BY measuredAt DESC", location)
                .page(0, limit)
                .list();
    }
}
