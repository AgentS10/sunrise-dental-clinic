package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A second, independent observer proving new reactions can be plugged in without
 * touching AppointmentService or NotificationObserver. Writes a structured audit
 * trail entry (clinic ethical/regulatory requirement: every new appointment must
 * be traceable to who/what created it and when).
 */
@Component
public class AuditLogObserver implements AppointmentObserver {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

    @Override
    public void onAppointmentRegistered(Appointment appointment) {
        auditLog.info("APPOINTMENT_CREATED number={} patient={} dentist={} date={} time={} treatment={}",
                appointment.getAppointmentNumber(),
                appointment.getPatient().getName(),
                appointment.getDentist().getName(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getTreatmentType().getName());
    }
}
