package ro.sergiu.georiskai.support;

import io.quarkus.arc.Arc;
import io.quarkus.test.junit.callback.QuarkusTestBeforeEachCallback;
import io.quarkus.test.junit.callback.QuarkusTestMethodContext;
import ro.sergiu.georiskai.location.repository.LocationRepository;
import ro.sergiu.georiskai.risk.repository.RiskAssessmentRepository;
import ro.sergiu.georiskai.weather.repository.WeatherMeasurementRepository;

/**
 * Keeps integration and endpoint tests isolated from data created by previous tests.
 */
public final class DatabaseCleanupCallback implements QuarkusTestBeforeEachCallback {

    @Override
    public void beforeEach(QuarkusTestMethodContext context) {
        Arc.container().instance(RiskAssessmentRepository.class).get().deleteAll();
        Arc.container().instance(WeatherMeasurementRepository.class).get().deleteAll();
        Arc.container().instance(LocationRepository.class).get().deleteAll();
    }
}
