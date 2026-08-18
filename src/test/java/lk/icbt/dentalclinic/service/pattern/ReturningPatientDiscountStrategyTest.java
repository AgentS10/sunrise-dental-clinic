package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.TreatmentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TDD note: written before the loyalty-discount rule was implemented, to lock down
 * the boundary condition (exactly 3 completed appointments should already qualify)
 * and the exact rounding behaviour, before ReturningPatientDiscountStrategy existed.
 */
class ReturningPatientDiscountStrategyTest {

    private final ReturningPatientDiscountStrategy strategy = new ReturningPatientDiscountStrategy();

    @Test
    void appliesTo_isFalse_belowLoyaltyThreshold() {
        assertThat(strategy.appliesTo(0)).isFalse();
        assertThat(strategy.appliesTo(2)).isFalse();
    }

    @Test
    void appliesTo_isTrue_atAndAboveLoyaltyThreshold() {
        assertThat(strategy.appliesTo(3)).isTrue();
        assertThat(strategy.appliesTo(10)).isTrue();
    }

    @Test
    void calculate_applies10PercentDiscountToTreatmentFeeOnly() {
        TreatmentType rootCanal = TreatmentType.builder().name("Root Canal").fee(new BigDecimal("15000.00")).build();

        BillingResult result = strategy.calculate(new BigDecimal("1500.00"), rootCanal, 5);

        assertThat(result.discountAmount()).isEqualByComparingTo("1500.00"); // 10% of 15000
        assertThat(result.consultationFee()).isEqualByComparingTo("1500.00"); // unaffected
        assertThat(result.totalAmount()).isEqualByComparingTo("15000.00"); // 1500 + 15000 - 1500
        assertThat(result.strategyName()).contains("Loyalty");
    }
}
