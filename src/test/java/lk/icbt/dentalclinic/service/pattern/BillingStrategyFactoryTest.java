package lk.icbt.dentalclinic.service.pattern;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BillingStrategyFactoryTest {

    private final BillingStrategyFactory factory = new BillingStrategyFactory(
            List.of(new ReturningPatientDiscountStrategy(), new StandardBillingStrategy()));

    @Test
    void resolve_picksLoyaltyStrategy_whenPatientQualifies() {
        BillingStrategy resolved = factory.resolve(4);
        assertThat(resolved).isInstanceOf(ReturningPatientDiscountStrategy.class);
    }

    @Test
    void resolve_fallsBackToStandardStrategy_whenPatientDoesNotQualify() {
        BillingStrategy resolved = factory.resolve(1);
        assertThat(resolved).isInstanceOf(StandardBillingStrategy.class);
    }
}
