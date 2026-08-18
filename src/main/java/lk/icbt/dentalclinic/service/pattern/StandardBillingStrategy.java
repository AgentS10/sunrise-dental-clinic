package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.TreatmentType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Default pricing rule: consultation fee + treatment fee, no discount.
 * Registered last (lowest priority via @Order) so more specific strategies
 * (e.g. {@link ReturningPatientDiscountStrategy}) are tried first by the
 * {@link BillingStrategyFactory}.
 */
@Component
@Order(Integer.MAX_VALUE)
public class StandardBillingStrategy implements BillingStrategy {

    @Override
    public boolean appliesTo(long completedAppointmentsForPatient) {
        return true; // fallback: always applicable
    }

    @Override
    public BillingResult calculate(BigDecimal consultationFee, TreatmentType treatmentType,
                                    long completedAppointmentsForPatient) {
        BigDecimal treatmentFee = treatmentType.getFee();
        BigDecimal total = consultationFee.add(treatmentFee);
        return new BillingResult(consultationFee, treatmentFee, BigDecimal.ZERO, total, name());
    }

    @Override
    public String name() {
        return "Standard Billing";
    }
}
