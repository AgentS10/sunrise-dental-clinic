package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.Appointment;

/**
 * OBSERVER PATTERN - observer role.
 * Implementations react to an appointment being registered without the
 * appointment-registration code needing to know they exist.
 */
public interface AppointmentObserver {
    void onAppointmentRegistered(Appointment appointment);
}
