package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.Appointment;
import lk.icbt.dentalclinic.domain.Bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * BUILDER PATTERN.
 * <p>
 * Constructs an immutable-in-effect {@link Bill} step by step through a fluent API,
 * validating that every required part has been supplied before {@link #build()}
 * releases the object. This keeps {@code Bill} free of a large telescoping
 * constructor and keeps bill-assembly logic (rounding, defaulting, validation)
 * out of the service layer.
 * <p>
 * Written by hand (rather than relying on the {@code @lombok.Builder} used on the
 * entity classes for simple data carriers) specifically to demonstrate the GoF
 * Builder pattern, since it performs real assembly logic - deriving the bill
 * number and generation timestamp - rather than just setting fields.
 */
public class BillBuilder {

    private Appointment appointment;
    private BigDecimal consultationFee;
    private BigDecimal treatmentFee;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal totalAmount;
    private String pricingStrategyUsed = "Standard Billing";

    public static BillBuilder newBill() {
        return new BillBuilder();
    }

    public BillBuilder forAppointment(Appointment appointment) {
        this.appointment = appointment;
        return this;
    }

    public BillBuilder withConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
        return this;
    }

    public BillBuilder withTreatmentFee(BigDecimal treatmentFee) {
        this.treatmentFee = treatmentFee;
        return this;
    }

    public BillBuilder withDiscount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount == null ? BigDecimal.ZERO : discountAmount;
        return this;
    }

    public BillBuilder withTotal(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public BillBuilder withPricingStrategyUsed(String pricingStrategyUsed) {
        this.pricingStrategyUsed = pricingStrategyUsed;
        return this;
    }

    public BillBuilder fromPricing(BillingResult pricing) {
        this.consultationFee = pricing.consultationFee();
        this.treatmentFee = pricing.treatmentFee();
        this.discountAmount = pricing.discountAmount();
        this.totalAmount = pricing.totalAmount();
        this.pricingStrategyUsed = pricing.strategyName();
        return this;
    }

    public Bill build() {
        Objects.requireNonNull(appointment, "appointment is required to build a Bill");
        Objects.requireNonNull(consultationFee, "consultationFee is required to build a Bill");
        Objects.requireNonNull(treatmentFee, "treatmentFee is required to build a Bill");
        Objects.requireNonNull(totalAmount, "totalAmount is required to build a Bill");

        String billNumber = "BILL-" + appointment.getAppointmentNumber().replace("APT-", "");

        return Bill.builder()
                .billNumber(billNumber)
                .appointment(appointment)
                .consultationFee(consultationFee)
                .treatmentFee(treatmentFee)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .pricingStrategyUsed(pricingStrategyUsed)
                .generatedAt(LocalDateTime.now())
                .build();
    }
}
