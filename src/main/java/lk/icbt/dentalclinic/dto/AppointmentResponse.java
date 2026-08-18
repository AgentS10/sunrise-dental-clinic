package lk.icbt.dentalclinic.dto;

import lk.icbt.dentalclinic.domain.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Read-only projection of an Appointment for Functionality 3 (Display Appointment
 * Details) - used by both the Thymeleaf view and the JSON REST API so the two
 * presentation channels never disagree on shape.
 */
public record AppointmentResponse(
        String appointmentNumber,
        String patientName,
        String patientAddress,
        String patientContact,
        String dentistName,
        String treatmentType,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        String status,
        String notes
) {
    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getAppointmentNumber(),
                a.getPatient().getName(),
                a.getPatient().getAddress(),
                a.getPatient().getContactNumber(),
                a.getDentist().getName(),
                a.getTreatmentType().getName(),
                a.getAppointmentDate(),
                a.getAppointmentTime(),
                a.getStatus().name(),
                a.getNotes());
    }
}
