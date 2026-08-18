package lk.icbt.dentalclinic.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data-bound form / API request for Functionality 2: Register New Appointment.
 * Bean Validation annotations implement the "proper validation mechanisms" required
 * by the brief; messages are surfaced back to the user-friendly form (see
 * templates/appointment-form.html) or as a structured 400 response from the REST API.
 */
@Getter
@Setter
@NoArgsConstructor
public class RegisterAppointmentRequest {

    /** If set, reuse an existing registered patient instead of creating a new one. */
    private Long existingPatientId;

    @NotBlank(message = "Patient name is required")
    @Size(min = 2, max = 100, message = "Patient name must be between 2 and 100 characters")
    @Pattern(regexp = "^[A-Za-z .'-]+$", message = "Patient name may only contain letters, spaces, apostrophes and hyphens")
    private String patientName;

    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address must be at most 200 characters")
    private String address;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^0\\d{9}$", message = "Contact number must be a valid 10-digit Sri Lankan number starting with 0 (e.g. 0771234567)")
    private String contactNumber;

    @NotNull(message = "Please select a dentist")
    private Long dentistId;

    @NotNull(message = "Please select a treatment type")
    private Long treatmentTypeId;

    @NotNull(message = "Appointment date is required")
    @FutureOrPresent(message = "Appointment date cannot be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime appointmentTime;

    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;
}
