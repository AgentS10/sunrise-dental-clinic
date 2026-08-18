package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.domain.Appointment;
import lk.icbt.dentalclinic.domain.Bill;
import lk.icbt.dentalclinic.domain.Dentist;
import lk.icbt.dentalclinic.domain.TreatmentType;
import lk.icbt.dentalclinic.dto.AppointmentResponse;
import lk.icbt.dentalclinic.dto.BillResponse;
import lk.icbt.dentalclinic.dto.RegisterAppointmentRequest;
import lk.icbt.dentalclinic.repository.DentistRepository;
import lk.icbt.dentalclinic.repository.TreatmentTypeRepository;
import lk.icbt.dentalclinic.service.pattern.ReportResult;
import lk.icbt.dentalclinic.service.pattern.ReportType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * FACADE PATTERN.
 * <p>
 * {@code AppointmentService}, {@code BillingService}, {@code PatientService},
 * {@code ReportService} and two lookup repositories together form the clinic's
 * "subsystem". Web and REST controllers should not need to know about, wire up,
 * or coordinate all of them individually - {@code ClinicFacade} exposes exactly
 * the operations the presentation layer needs (register an appointment, look one
 * up, bill it, run a report) as a single simplified entry point, keeping the
 * three-tier boundary between presentation and business logic clean.
 * <p>
 * Read methods are wrapped in a transaction here (rather than left to the
 * repository/service layer alone) so that lazily-fetched associations
 * (patient, dentist, treatment type) are still initialisable while they are
 * mapped into DTOs below - {@code spring.jpa.open-in-view} is deliberately
 * disabled, so without this the mapping would fail outside any active session.
 */
@Service
@Transactional(readOnly = true)
public class ClinicFacade {

    private final AppointmentService appointmentService;
    private final BillingService billingService;
    private final ReportService reportService;
    private final DentistRepository dentistRepository;
    private final TreatmentTypeRepository treatmentTypeRepository;

    public ClinicFacade(AppointmentService appointmentService,
                         BillingService billingService,
                         ReportService reportService,
                         DentistRepository dentistRepository,
                         TreatmentTypeRepository treatmentTypeRepository) {
        this.appointmentService = appointmentService;
        this.billingService = billingService;
        this.reportService = reportService;
        this.dentistRepository = dentistRepository;
        this.treatmentTypeRepository = treatmentTypeRepository;
    }

    @Transactional
    public AppointmentResponse registerAppointment(RegisterAppointmentRequest request) {
        Appointment appointment = appointmentService.register(request);
        return AppointmentResponse.from(appointment);
    }

    public AppointmentResponse findAppointment(String appointmentNumber) {
        Appointment appointment = appointmentService.findByAppointmentNumber(appointmentNumber);
        return AppointmentResponse.from(appointment);
    }

    public List<AppointmentResponse> appointmentsForDate(LocalDate date) {
        return appointmentService.findByDate(date).stream().map(AppointmentResponse::from).toList();
    }

    @Transactional
    public AppointmentResponse cancelAppointment(String appointmentNumber) {
        Appointment appointment = appointmentService.cancel(appointmentNumber);
        return AppointmentResponse.from(appointment);
    }

    @Transactional // may write on first call: generates and persists the bill lazily
    public BillResponse billFor(String appointmentNumber) {
        Bill bill = billingService.getByAppointmentNumber(appointmentNumber);
        return BillResponse.from(bill);
    }

    public ReportResult report(ReportType type, LocalDate from, LocalDate to) {
        return reportService.generate(type, from, to);
    }

    public List<ReportType> availableReportTypes() {
        return reportService.availableReportTypes();
    }

    public List<Dentist> activeDentists() {
        return dentistRepository.findByActiveTrue();
    }

    public List<TreatmentType> treatmentTypes() {
        return treatmentTypeRepository.findAll();
    }
}
