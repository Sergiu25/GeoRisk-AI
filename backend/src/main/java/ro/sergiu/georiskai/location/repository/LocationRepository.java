package ro.sergiu.georiskai.location.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ro.sergiu.georiskai.location.entity.Location;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LocationRepository implements PanacheRepository<Location> {

    @Override
    @Transactional
    public long deleteAll() {
        return getEntityManager()
                .createQuery("delete from Location")
                .executeUpdate();
    }

    public Optional<Location> findByCoordinates(BigDecimal latitude, BigDecimal longitude) {
        return find("latitude = ?1 AND longitude = ?2", latitude, longitude).firstResultOptional();
    }

    public boolean existsByCoordinatesExcludingId(BigDecimal latitude, BigDecimal longitude, Long id) {
        return count("latitude = ?1 AND longitude = ?2 AND id <> ?3", latitude, longitude, id) > 0;
    }

    public List<Location> findByCountry(String country) {
        return find("LOWER(country) = LOWER(?1) ORDER BY name", country).list();
    }

    public List<Location> findByName(String name) {
        return find("LOWER(name) LIKE LOWER(?1) ORDER BY name", "%" + name + "%").list();
    }
}
