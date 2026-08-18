package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.TreatmentType;

import java.math.BigDecimal;

/**
 * STRATEGY PATTERN.
 * <p>
 * Encapsulates one algorithm for turning a treatment + consultation fee into a
 * priced bill. New pricing rules (seasonal promotions, insurance co-pay, corporate
 * billing, etc.) can be added as new implementations without touching
 * {@code BillingService} or any other class that already depends on this interface
 * (Open/Closed Principle).
 */
public interface BillingStrategy {

    /** Whether this strategy is the correct one to apply for a patient with this history. */
    boolean appliesTo(long completedAppointmentsForPatient);

    BillingResult calculate(BigDecimal consultationFee, TreatmentType treatmentType,
                             long completedAppointmentsForPatient);

    String name();
}
