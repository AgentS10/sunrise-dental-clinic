package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.domain.*;
import lk.icbt.dentalclinic.repository.BillRepository;
import lk.icbt.dentalclinic.service.pattern.BillingResult;
import lk.icbt.dentalclinic.service.pattern.BillingStrategy;
import lk.icbt.dentalclinic.service.pattern.BillingStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock private BillRepository billRepository;
    @Mock private AppointmentService appointmentService;
    @Mock private BillingStrategyFactory billingStrategyFactory;
    @Mock private BillingStrategy billingStrategy;

    private BillingService billingService;

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        billingService = new BillingService(billRepository, appointmentService, billingStrategyFactory,
                new BigDecimal("1500.00"));

        Patient patient = Patient.builder().id(1L).name("Kasun Silva").build();
        TreatmentType treatmentType = TreatmentType.builder().name("Scaling").fee(new BigDecimal("3500.00")).build();
        appointment = Appointment.builder()
                .appointmentNumber("APT-000001")
                .patient(patient)
                .treatmentType(treatmentType)
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }

    @Test
    void generateBill_createsANewBill_usingTheResolvedStrategy() {
        when(billRepository.findByAppointment_AppointmentNumber("APT-000001")).thenReturn(Optional.empty());
        when(appointmentService.findByAppointmentNumber("APT-000001")).thenReturn(appointment);
        when(appointmentService.countCompletedForPatient(1L)).thenReturn(0L);
        when(billingStrategyFactory.resolve(0L)).thenReturn(billingStrategy);
        when(billingStrategy.calculate(eq(new BigDecimal("1500.00")), eq(appointment.getTreatmentType()), eq(0L)))
                .thenReturn(new BillingResult(new BigDecimal("1500.00"), new BigDecimal("3500.00"),
                        BigDecimal.ZERO, new BigDecimal("5000.00"), "Standard Billing"));
        when(billRepository.save(any(Bill.class))).thenAnswer(inv -> inv.getArgument(0));

        Bill bill = billingService.generateBill("APT-000001");

        assertThat(bill.getTotalAmount()).isEqualByComparingTo("5000.00");
        assertThat(bill.getBillNumber()).isEqualTo("BILL-000001");
        verify(appointmentService).markCompleted("APT-000001");
    }

    @Test
    void generateBill_isIdempotent_returnsExistingBillInsteadOfCreatingASecondOne() {
        Bill existing = Bill.builder().billNumber("BILL-000001").generatedAt(LocalDateTime.now())
                .totalAmount(new BigDecimal("5000.00")).build();
        when(billRepository.findByAppointment_AppointmentNumber("APT-000001")).thenReturn(Optional.of(existing));

        Bill result = billingService.generateBill("APT-000001");

        assertThat(result).isSameAs(existing);
        verifyNoInteractions(appointmentService, billingStrategyFactory);
        verify(billRepository, never()).save(any());
    }
}
