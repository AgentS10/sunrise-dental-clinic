package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.domain.AppointmentStatus;
import lk.icbt.dentalclinic.domain.Bill;
import lk.icbt.dentalclinic.domain.Appointment;
import lk.icbt.dentalclinic.repository.BillRepository;
import lk.icbt.dentalclinic.service.pattern.BillBuilder;
import lk.icbt.dentalclinic.service.pattern.BillingResult;
import lk.icbt.dentalclinic.service.pattern.BillingStrategy;
import lk.icbt.dentalclinic.service.pattern.BillingStrategyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Functionality 4: Calculate and Print Bill. Selects the correct
 * {@link BillingStrategy} via {@link BillingStrategyFactory} (Strategy + Factory
 * patterns), assembles the {@link Bill} via {@link BillBuilder} (Builder pattern),
 * and marks the appointment as completed once billed.
 */
@Service
@Transactional
public class BillingService {

    private final BillRepository billRepository;
    private final AppointmentService appointmentService;
    private final BillingStrategyFactory billingStrategyFactory;
    private final BigDecimal consultationFee;

    public BillingService(BillRepository billRepository,
                           AppointmentService appointmentService,
                           BillingStrategyFactory billingStrategyFactory,
                           @Value("${clinic.consultation-fee}") BigDecimal consultationFee) {
        this.billRepository = billRepository;
        this.appointmentService = appointmentService;
        this.billingStrategyFactory = billingStrategyFactory;
        this.consultationFee = consultationFee;
    }

    public Bill generateBill(String appointmentNumber) {
        return billRepository.findByAppointment_AppointmentNumber(appointmentNumber)
                .orElseGet(() -> createBill(appointmentNumber));
    }

    private Bill createBill(String appointmentNumber) {
        Appointment appointment = appointmentService.findByAppointmentNumber(appointmentNumber);

        long completed = appointmentService.countCompletedForPatient(appointment.getPatient().getId());
        BillingStrategy strategy = billingStrategyFactory.resolve(completed);
        BillingResult pricing = strategy.calculate(consultationFee, appointment.getTreatmentType(), completed);

        Bill bill = BillBuilder.newBill()
                .forAppointment(appointment)
                .fromPricing(pricing)
                .build();

        Bill saved = billRepository.save(bill);

        if (appointment.getStatus() != AppointmentStatus.CANCELLED) {
            appointmentService.markCompleted(appointmentNumber);
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public Bill getByAppointmentNumber(String appointmentNumber) {
        return billRepository.findByAppointment_AppointmentNumber(appointmentNumber)
                .orElseGet(() -> createBill(appointmentNumber));
    }
}
