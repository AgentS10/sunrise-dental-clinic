package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.Appointment;
import lk.icbt.dentalclinic.domain.Notification;
import lk.icbt.dentalclinic.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simulates sending an SMS confirmation to the patient when their appointment is
 * registered, and persists the outbound message for audit/demo purposes.
 * See {@link lk.icbt.dentalclinic.domain.Notification} for why this is simulated
 * rather than wired to a real gateway.
 */
@Component
public class NotificationObserver implements AppointmentObserver {

    private static final Logger log = LoggerFactory.getLogger(NotificationObserver.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm");

    private final NotificationRepository notificationRepository;

    public NotificationObserver(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void onAppointmentRegistered(Appointment appointment) {
        String message = String.format(
                "Dear %s, your appointment %s with %s is confirmed for %s. - Sunrise Dental Clinic",
                appointment.getPatient().getName(),
                appointment.getAppointmentNumber(),
                appointment.getDentist().getName(),
                appointment.getAppointmentDate().atTime(appointment.getAppointmentTime()).format(FMT));

        Notification notification = Notification.builder()
                .appointmentNumber(appointment.getAppointmentNumber())
                .channel("SMS")
                .message(message)
                .sentAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        log.info("[SIMULATED SMS to {}] {}", appointment.getPatient().getContactNumber(), message);
    }
}
