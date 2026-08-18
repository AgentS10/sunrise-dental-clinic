package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.domain.*;
import lk.icbt.dentalclinic.dto.RegisterAppointmentRequest;
import lk.icbt.dentalclinic.exception.AppointmentNotFoundException;
import lk.icbt.dentalclinic.exception.DoubleBookingException;
import lk.icbt.dentalclinic.exception.InvalidAppointmentException;
import lk.icbt.dentalclinic.repository.AppointmentRepository;
import lk.icbt.dentalclinic.repository.DentistRepository;
import lk.icbt.dentalclinic.repository.TreatmentTypeRepository;
import lk.icbt.dentalclinic.service.pattern.AppointmentEventPublisher;
import lk.icbt.dentalclinic.service.pattern.AppointmentNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Functionalities 2 & 3 from the brief: register a new appointment and look one up
 * by appointment number. Delegates patient de-duplication to {@link PatientService},
 * appointment numbering to the {@link AppointmentNumberGenerator} Singleton, and
 * post-registration side effects to {@link AppointmentEventPublisher} (Observer).
 */
@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentTypeRepository treatmentTypeRepository;
    private final PatientService patientService;
    private final AppointmentEventPublisher eventPublisher;

    public AppointmentService(AppointmentRepository appointmentRepository,
                               DentistRepository dentistRepository,
                               TreatmentTypeRepository treatmentTypeRepository,
                               PatientService patientService,
                               AppointmentEventPublisher eventPublisher) {
        this.appointmentRepository = appointmentRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentTypeRepository = treatmentTypeRepository;
        this.patientService = patientService;
        this.eventPublisher = eventPublisher;
    }

    public Appointment register(RegisterAppointmentRequest request) {
        validateBusinessRules(request);

        Dentist dentist = dentistRepository.findById(request.getDentistId())
                .orElseThrow(() -> new InvalidAppointmentException("Selected dentist does not exist"));
        TreatmentType treatmentType = treatmentTypeRepository.findById(request.getTreatmentTypeId())
                .orElseThrow(() -> new InvalidAppointmentException("Selected treatment type does not exist"));

        Patient patient = request.getExistingPatientId() != null
                ? patientService.getById(request.getExistingPatientId())
                : patientService.findOrRegister(request.getPatientName(), request.getAddress(), request.getContactNumber());

        Appointment appointment = Appointment.builder()
                .appointmentNumber(AppointmentNumberGenerator.getInstance().nextAppointmentNumber())
                .patient(patient)
                .dentist(dentist)
                .treatmentType(treatmentType)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .status(AppointmentStatus.SCHEDULED)
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        eventPublisher.publishAppointmentRegistered(saved);
        return saved;
    }

    /** Functionality 3: Display Appointment Details - search using the appointment number. */
    @Transactional(readOnly = true)
    public Appointment findByAppointmentNumber(String appointmentNumber) {
        return appointmentRepository.findByAppointmentNumber(appointmentNumber)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentNumber));
    }

    @Transactional(readOnly = true)
    public List<Appointment> findByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDateOrderByAppointmentTimeAsc(date);
    }

    public Appointment markCompleted(String appointmentNumber) {
        Appointment appointment = findByAppointmentNumber(appointmentNumber);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    public Appointment cancel(String appointmentNumber) {
        Appointment appointment = findByAppointmentNumber(appointmentNumber);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public long countCompletedForPatient(Long patientId) {
        return appointmentRepository.countByPatientIdAndStatus(patientId, AppointmentStatus.COMPLETED);
    }

    private void validateBusinessRules(RegisterAppointmentRequest request) {
        if (request.getAppointmentDate().equals(LocalDate.now())
                && request.getAppointmentTime().isBefore(LocalTime.now())) {
            throw new InvalidAppointmentException("Appointment time cannot be in the past for today's date");
        }

        boolean clash = appointmentRepository
                .findByAppointmentDateOrderByAppointmentTimeAsc(request.getAppointmentDate())
                .stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .anyMatch(a -> a.getDentist().getId().equals(request.getDentistId())
                        && a.getAppointmentTime().equals(request.getAppointmentTime()));

        if (clash) {
            Dentist dentist = dentistRepository.findById(request.getDentistId()).orElse(null);
            String dentistName = dentist != null ? dentist.getName() : "selected dentist";
            throw new DoubleBookingException(dentistName,
                    request.getAppointmentDate().toString(), request.getAppointmentTime().toString());
        }
    }
}
