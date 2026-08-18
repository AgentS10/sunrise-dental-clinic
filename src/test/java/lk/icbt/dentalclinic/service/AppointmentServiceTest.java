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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Functionalities 2 & 3 (register / display appointment), and the
 * scenario's "double bookings" problem specifically. Collaborators are mocked so
 * these tests exercise AppointmentService's own logic in isolation, per the TDD
 * approach documented in the test plan.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private DentistRepository dentistRepository;
    @Mock private TreatmentTypeRepository treatmentTypeRepository;
    @Mock private PatientService patientService;
    @Mock private AppointmentEventPublisher eventPublisher;

    @InjectMocks
    private AppointmentService appointmentService;

    private Dentist dentist;
    private TreatmentType treatmentType;
    private Patient patient;

    @BeforeEach
    void setUp() {
        AppointmentNumberGenerator.getInstance().resetForTesting();
        dentist = Dentist.builder().id(1L).name("Dr. Nimal Perera").specialization("General").build();
        treatmentType = TreatmentType.builder().id(1L).name("Scaling").fee(new BigDecimal("3500.00")).build();
        patient = Patient.builder().id(1L).name("Kasun Silva").address("Colombo").contactNumber("0771234567").build();
    }

    private RegisterAppointmentRequest validRequest() {
        RegisterAppointmentRequest request = new RegisterAppointmentRequest();
        request.setPatientName("Kasun Silva");
        request.setAddress("Colombo");
        request.setContactNumber("0771234567");
        request.setDentistId(1L);
        request.setTreatmentTypeId(1L);
        request.setAppointmentDate(LocalDate.now().plusDays(1));
        request.setAppointmentTime(LocalTime.of(10, 0));
        return request;
    }

    @Test
    void register_savesAppointment_withGeneratedNumber_andPublishesEvent() {
        when(appointmentRepository.findByAppointmentDateOrderByAppointmentTimeAsc(any())).thenReturn(List.of());
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentTypeRepository.findById(1L)).thenReturn(Optional.of(treatmentType));
        when(patientService.findOrRegister(any(), any(), any())).thenReturn(patient);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        Appointment result = appointmentService.register(validRequest());

        assertThat(result.getAppointmentNumber()).isEqualTo("APT-000001");
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        verify(eventPublisher).publishAppointmentRegistered(result);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void register_throwsDoubleBookingException_whenDentistAlreadyBookedAtThatTime() {
        RegisterAppointmentRequest request = validRequest();
        Appointment clashing = Appointment.builder()
                .dentist(dentist)
                .appointmentTime(request.getAppointmentTime())
                .status(AppointmentStatus.SCHEDULED)
                .build();
        when(appointmentRepository.findByAppointmentDateOrderByAppointmentTimeAsc(any()))
                .thenReturn(List.of(clashing));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));

        assertThatThrownBy(() -> appointmentService.register(request))
                .isInstanceOf(DoubleBookingException.class)
                .hasMessageContaining("already has an appointment");

        verify(appointmentRepository, never()).save(any());
        verify(eventPublisher, never()).publishAppointmentRegistered(any());
    }

    @Test
    void register_ignoresCancelledAppointments_whenCheckingForDoubleBooking() {
        RegisterAppointmentRequest request = validRequest();
        Appointment cancelled = Appointment.builder()
                .dentist(dentist)
                .appointmentTime(request.getAppointmentTime())
                .status(AppointmentStatus.CANCELLED)
                .build();
        when(appointmentRepository.findByAppointmentDateOrderByAppointmentTimeAsc(any()))
                .thenReturn(List.of(cancelled));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentTypeRepository.findById(1L)).thenReturn(Optional.of(treatmentType));
        when(patientService.findOrRegister(any(), any(), any())).thenReturn(patient);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        Appointment result = appointmentService.register(request);

        assertThat(result).isNotNull();
    }

    @Test
    void register_throwsInvalidAppointmentException_whenTimeIsInThePastToday() {
        RegisterAppointmentRequest request = validRequest();
        request.setAppointmentDate(LocalDate.now());
        request.setAppointmentTime(LocalTime.MIN); // midnight - always in the past once the day has started

        assertThatThrownBy(() -> appointmentService.register(request))
                .isInstanceOf(InvalidAppointmentException.class);

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void findByAppointmentNumber_throwsNotFoundException_whenAppointmentDoesNotExist() {
        when(appointmentRepository.findByAppointmentNumber("APT-999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.findByAppointmentNumber("APT-999999"))
                .isInstanceOf(AppointmentNotFoundException.class)
                .hasMessageContaining("APT-999999");
    }

    @Test
    void findByAppointmentNumber_returnsAppointment_whenItExists() {
        Appointment appointment = Appointment.builder().appointmentNumber("APT-000001").build();
        when(appointmentRepository.findByAppointmentNumber("APT-000001")).thenReturn(Optional.of(appointment));

        Appointment result = appointmentService.findByAppointmentNumber("APT-000001");

        assertThat(result.getAppointmentNumber()).isEqualTo("APT-000001");
    }
}
