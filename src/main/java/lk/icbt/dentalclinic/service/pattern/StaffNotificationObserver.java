package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.Appointment;
import lk.icbt.dentalclinic.domain.StaffNotification;
import lk.icbt.dentalclinic.repository.StaffNotificationRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Third Observer implementation, added after the initial submission to answer
 * "how do staff find out what their colleagues just did" - every registration
 * and cancellation raises an in-app notification visible to every logged-in
 * staff member via the notification bell (fragments/nav.html), independent of
 * NotificationObserver (which simulates an SMS to the patient) and
 * AuditLogObserver (which writes to the audit log file). Adding this class
 * required zero changes to AppointmentService, AppointmentEventPublisher, or
 * either of the other two observers - exactly the extensibility the Observer
 * pattern is meant to provide.
 */
@Component
public class StaffNotificationObserver implements AppointmentObserver {

    private final StaffNotificationRepository staffNotificationRepository;

    public StaffNotificationObserver(StaffNotificationRepository staffNotificationRepository) {
        this.staffNotificationRepository = staffNotificationRepository;
    }

    @Override
    public void onAppointmentRegistered(Appointment appointment) {
        save("New appointment " + appointment.getAppointmentNumber() + " booked for "
                        + appointment.getPatient().getName() + " with " + appointment.getDentist().getName()
                        + " on " + appointment.getAppointmentDate() + " at " + appointment.getAppointmentTime(),
                appointment.getAppointmentNumber());
    }

    @Override
    public void onAppointmentCancelled(Appointment appointment) {
        save("Appointment " + appointment.getAppointmentNumber() + " for " + appointment.getPatient().getName()
                        + " was cancelled.",
                appointment.getAppointmentNumber());
    }

    private void save(String message, String appointmentNumber) {
        staffNotificationRepository.save(StaffNotification.builder()
                .message(message)
                .appointmentNumber(appointmentNumber)
                .createdAt(LocalDateTime.now())
                .build());
    }
}
