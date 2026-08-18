package lk.icbt.dentalclinic.api;

import lk.icbt.dentalclinic.dto.AppointmentResponse;
import lk.icbt.dentalclinic.dto.RegisterAppointmentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end test of the distributed REST web service (Task B.i): a real HTTP
 * client hits a running server over HTTP Basic auth and a real (in-memory) H2
 * database, exercising the full stack - security, validation, service layer,
 * design patterns and persistence together.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AppointmentApiIntegrationTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private TestRestTemplate asAdmin() {
        return restTemplate.withBasicAuth("admin", "Admin@123");
    }

    private RegisterAppointmentRequest sampleRequest(LocalTime time) {
        RegisterAppointmentRequest request = new RegisterAppointmentRequest();
        request.setPatientName("Nadeesha Perera");
        request.setAddress("Nugegoda, Colombo");
        request.setContactNumber("0719876543");
        request.setDentistId(1L);
        request.setTreatmentTypeId(1L);
        request.setAppointmentDate(LocalDate.now().plusDays(2));
        request.setAppointmentTime(time);
        return request;
    }

    @Test
    void unauthenticatedRequest_isRejected() {
        ResponseEntity<String> response = restTemplate.getForEntity(url("/api/appointments/APT-000001"), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void registerThenRetrieve_roundTripsSuccessfully() {
        ResponseEntity<AppointmentResponse> created = asAdmin()
                .postForEntity(url("/api/appointments"), sampleRequest(LocalTime.of(9, 0)), AppointmentResponse.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        String appointmentNumber = created.getBody().appointmentNumber();
        assertThat(appointmentNumber).startsWith("APT-");

        ResponseEntity<AppointmentResponse> fetched = asAdmin()
                .getForEntity(url("/api/appointments/" + appointmentNumber), AppointmentResponse.class);

        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetched.getBody().patientName()).isEqualTo("Nadeesha Perera");
    }

    @Test
    void registerConflictingAppointment_returns409() {
        LocalTime clashTime = LocalTime.of(11, 0);
        asAdmin().postForEntity(url("/api/appointments"), sampleRequest(clashTime), AppointmentResponse.class);

        ResponseEntity<String> conflict = asAdmin()
                .postForEntity(url("/api/appointments"), sampleRequest(clashTime), String.class);

        assertThat(conflict.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void registerWithInvalidContactNumber_returns400() {
        RegisterAppointmentRequest request = sampleRequest(LocalTime.of(14, 0));
        request.setContactNumber("123"); // fails the 10-digit pattern

        ResponseEntity<String> response = asAdmin()
                .postForEntity(url("/api/appointments"), request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getUnknownAppointment_returns404() {
        ResponseEntity<String> response = asAdmin()
                .getForEntity(url("/api/appointments/APT-999999"), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
