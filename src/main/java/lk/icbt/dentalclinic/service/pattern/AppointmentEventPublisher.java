package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * OBSERVER PATTERN - subject role.
 * <p>
 * {@code AppointmentService} calls {@link #publishAppointmentRegistered(Appointment)}
 * exactly once after persisting a new appointment. Every registered
 * {@link AppointmentObserver} bean (Spring collects them automatically) is then
 * notified. This decouples "an appointment was booked" from "what should happen
 * as a result" (notify patient, write an audit trail, etc.) - new reactions are
 * added by writing a new observer, never by editing AppointmentService.
 */
@Component
public class AppointmentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AppointmentEventPublisher.class);

    private final List<AppointmentObserver> observers;

    public AppointmentEventPublisher(List<AppointmentObserver> observers) {
        this.observers = observers;
    }

    public void publishAppointmentRegistered(Appointment appointment) {
        for (AppointmentObserver observer : observers) {
            try {
                observer.onAppointmentRegistered(appointment);
            } catch (Exception ex) {
                // an observer failing (e.g. simulated SMS gateway) must never roll back
                // the appointment that has already been safely persisted.
                log.warn("Observer {} failed for appointment {}: {}",
                        observer.getClass().getSimpleName(), appointment.getAppointmentNumber(), ex.getMessage());
            }
        }
    }
}
