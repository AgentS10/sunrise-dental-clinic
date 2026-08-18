package lk.icbt.dentalclinic.api;

import jakarta.validation.Valid;
import lk.icbt.dentalclinic.dto.AppointmentResponse;
import lk.icbt.dentalclinic.dto.BillResponse;
import lk.icbt.dentalclinic.dto.RegisterAppointmentRequest;
import lk.icbt.dentalclinic.service.ClinicFacade;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * JSON REST web service satisfying Task B.i ("distributed application with web
 * services"). Secured independently via HTTP Basic (see SecurityConfig) so it can
 * be consumed by any external client - not just the Thymeleaf UI in this same
 * deployment - which is what makes this a genuinely distributed architecture
 * rather than a monolith with two view technologies.
 */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentApiController {

    private final ClinicFacade clinicFacade;

    public AppointmentApiController(ClinicFacade clinicFacade) {
        this.clinicFacade = clinicFacade;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> register(@Valid @RequestBody RegisterAppointmentRequest request) {
        AppointmentResponse response = clinicFacade.registerAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{appointmentNumber}")
    public AppointmentResponse getByNumber(@PathVariable String appointmentNumber) {
        return clinicFacade.findAppointment(appointmentNumber);
    }

    @GetMapping
    public List<AppointmentResponse> byDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return clinicFacade.appointmentsForDate(date);
    }

    @GetMapping("/{appointmentNumber}/bill")
    public BillResponse getBill(@PathVariable String appointmentNumber) {
        return clinicFacade.billFor(appointmentNumber);
    }
}
