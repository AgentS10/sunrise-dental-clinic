package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.TreatmentType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Loyalty pricing rule: patients with 3 or more previously completed appointments
 * receive a 10% discount on the treatment fee (consultation fee is unaffected).
 * Assumption: this loyalty threshold and rate are not specified in the brief;
 * they are introduced to demonstrate the Strategy pattern with a genuinely
 * different, business-meaningful algorithm rather than a trivial variant.
 */
@Component
@Order(1)
public class ReturningPatientDiscountStrategy implements BillingStrategy {

    static final int LOYALTY_THRESHOLD = 3;
    static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.10");

    @Override
    public boolean appliesTo(long completedAppointmentsForPatient) {
        return completedAppointmentsForPatient >= LOYALTY_THRESHOLD;
    }

    @Override
    public BillingResult calculate(BigDecimal consultationFee, TreatmentType treatmentType,
                                    long completedAppointmentsForPatient) {
        BigDecimal treatmentFee = treatmentType.getFee();
        BigDecimal discount = treatmentFee.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = consultationFee.add(treatmentFee).subtract(discount);
        return new BillingResult(consultationFee, treatmentFee, discount, total, name());
    }

    @Override
    public String name() {
        return "Returning Patient Loyalty Discount (10%)";
    }
}
