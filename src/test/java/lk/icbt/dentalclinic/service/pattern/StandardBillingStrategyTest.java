package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.TreatmentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class StandardBillingStrategyTest {

    private final StandardBillingStrategy strategy = new StandardBillingStrategy();

    @Test
    void appliesTo_isAlwaysTrue_asTheFallbackStrategy() {
        assertThat(strategy.appliesTo(0)).isTrue();
        assertThat(strategy.appliesTo(50)).isTrue();
    }

    @Test
    void calculate_addsConsultationAndTreatmentFee_withNoDiscount() {
        TreatmentType filling = TreatmentType.builder().name("Tooth Filling").fee(new BigDecimal("5000.00")).build();

        BillingResult result = strategy.calculate(new BigDecimal("1500.00"), filling, 0);

        assertThat(result.consultationFee()).isEqualByComparingTo("1500.00");
        assertThat(result.treatmentFee()).isEqualByComparingTo("5000.00");
        assertThat(result.discountAmount()).isEqualByComparingTo("0.00");
        assertThat(result.totalAmount()).isEqualByComparingTo("6500.00");
        assertThat(result.strategyName()).isEqualTo("Standard Billing");
    }
}
