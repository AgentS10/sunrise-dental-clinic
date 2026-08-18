package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.Appointment;
import lk.icbt.dentalclinic.domain.Bill;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BillBuilderTest {

    @Test
    void build_derivesBillNumberFromAppointmentNumber() {
        Appointment appointment = Appointment.builder().appointmentNumber("APT-000042").build();

        Bill bill = BillBuilder.newBill()
                .forAppointment(appointment)
                .withConsultationFee(new BigDecimal("1500.00"))
                .withTreatmentFee(new BigDecimal("5000.00"))
                .withTotal(new BigDecimal("6500.00"))
                .build();

        assertThat(bill.getBillNumber()).isEqualTo("BILL-000042");
        assertThat(bill.getAppointment()).isEqualTo(appointment);
        assertThat(bill.getGeneratedAt()).isNotNull();
    }

    @Test
    void build_populatesFromPricingResultInOneStep() {
        Appointment appointment = Appointment.builder().appointmentNumber("APT-000007").build();
        BillingResult pricing = new BillingResult(
                new BigDecimal("1500.00"), new BigDecimal("15000.00"),
                new BigDecimal("1500.00"), new BigDecimal("15000.00"), "Loyalty Discount");

        Bill bill = BillBuilder.newBill().forAppointment(appointment).fromPricing(pricing).build();

        assertThat(bill.getDiscountAmount()).isEqualByComparingTo("1500.00");
        assertThat(bill.getPricingStrategyUsed()).isEqualTo("Loyalty Discount");
    }

    @Test
    void build_throwsWhenAppointmentIsMissing() {
        assertThatThrownBy(() -> BillBuilder.newBill()
                .withConsultationFee(BigDecimal.TEN)
                .withTreatmentFee(BigDecimal.TEN)
                .withTotal(BigDecimal.TEN)
                .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("appointment");
    }
}
