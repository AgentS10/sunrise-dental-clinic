package lk.icbt.dentalclinic.dto;

import lk.icbt.dentalclinic.domain.Bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillResponse(
        String billNumber,
        String appointmentNumber,
        String patientName,
        BigDecimal consultationFee,
        BigDecimal treatmentFee,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        String pricingStrategyUsed,
        LocalDateTime generatedAt
) {
    public static BillResponse from(Bill b) {
        return new BillResponse(
                b.getBillNumber(),
                b.getAppointment().getAppointmentNumber(),
                b.getAppointment().getPatient().getName(),
                b.getConsultationFee(),
                b.getTreatmentFee(),
                b.getDiscountAmount(),
                b.getTotalAmount(),
                b.getPricingStrategyUsed(),
                b.getGeneratedAt());
    }
}
