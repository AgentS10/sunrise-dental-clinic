package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.*;
import lk.icbt.dentalclinic.repository.StaffNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * TDD note: written before wiring StaffNotificationObserver into
 * AppointmentEventPublisher, to pin down exactly what message text and
 * appointment-number linkage each event type should produce.
 */
@ExtendWith(MockitoExtension.class)
class StaffNotificationObserverTest {

    @Mock
    private StaffNotificationRepository staffNotificationRepository;

    private StaffNotificationObserver observer;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        observer = new StaffNotificationObserver(staffNotificationRepository);
        Patient patient = Patient.builder().name("Kasun Silva").build();
        Dentist dentist = Dentist.builder().name("Dr. Nimal Perera").build();
        appointment = Appointment.builder()
                .appointmentNumber("APT-000042")
                .patient(patient)
                .dentist(dentist)
                .appointmentDate(LocalDate.of(2026, 9, 1))
                .appointmentTime(LocalTime.of(10, 30))
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }

    @Test
    void onAppointmentRegistered_savesNotificationMentioningPatientDentistAndNumber() {
        observer.onAppointmentRegistered(appointment);

        ArgumentCaptor<StaffNotification> captor = ArgumentCaptor.forClass(StaffNotification.class);
        verify(staffNotificationRepository).save(captor.capture());

        StaffNotification saved = captor.getValue();
        assertThat(saved.getAppointmentNumber()).isEqualTo("APT-000042");
        assertThat(saved.getMessage()).contains("APT-000042", "Kasun Silva", "Dr. Nimal Perera");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void onAppointmentCancelled_savesNotificationMentioningCancellation() {
        observer.onAppointmentCancelled(appointment);

        ArgumentCaptor<StaffNotification> captor = ArgumentCaptor.forClass(StaffNotification.class);
        verify(staffNotificationRepository).save(captor.capture());

        assertThat(captor.getValue().getMessage()).containsIgnoringCase("cancelled");
    }
}
