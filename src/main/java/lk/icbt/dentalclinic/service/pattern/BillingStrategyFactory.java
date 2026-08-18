package lk.icbt.dentalclinic.service.pattern;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FACTORY (simple factory) selecting the right {@link BillingStrategy} at runtime.
 * <p>
 * Spring injects every {@code BillingStrategy} bean in {@code @Order} priority,
 * so adding a new strategy class is enough to plug it into billing - this factory
 * never needs to change. Callers depend only on this factory and the
 * {@link BillingStrategy} interface, never on a concrete strategy class.
 */
@Component
public class BillingStrategyFactory {

    private final List<BillingStrategy> strategies;

    public BillingStrategyFactory(List<BillingStrategy> strategies) {
        this.strategies = strategies;
    }

    public BillingStrategy resolve(long completedAppointmentsForPatient) {
        return strategies.stream()
                .filter(s -> s.appliesTo(completedAppointmentsForPatient))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No billing strategy available"));
    }
}
