package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.Appointment;

/**
 * OBSERVER PATTERN - observer role.
 * Implementations react to appointment lifecycle events without the
 * appointment-registration/cancellation code needing to know they exist.
 * onAppointmentCancelled has a default no-op body so existing observers
 * (NotificationObserver, AuditLogObserver) did not need to change when
 * cancellation notifications were added later - only StaffNotificationObserver
 * needed to react to it.
 */
public interface AppointmentObserver {
    void onAppointmentRegistered(Appointment appointment);

    default void onAppointmentCancelled(Appointment appointment) {
        // no-op by default
    }
}
