package lk.icbt.dentalclinic.service.pattern;

import java.math.BigDecimal;

/**
 * Immutable value object returned by a {@link BillingStrategy}.
 */
public record BillingResult(
        BigDecimal consultationFee,
        BigDecimal treatmentFee,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        String strategyName
) {
}
