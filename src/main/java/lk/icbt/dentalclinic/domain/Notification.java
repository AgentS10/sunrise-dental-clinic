package lk.icbt.dentalclinic.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A record of a notification "sent" to a patient when an appointment is booked.
 * Populated by NotificationObserver (Observer pattern). Assumption: no real SMS/email
 * gateway is available in this environment, so dispatch is simulated and persisted here
 * for audit/demo purposes - the observer interface makes swapping in a real gateway
 * (e.g. Twilio, JavaMail) a one-class change with no impact on AppointmentService.
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String appointmentNumber;

    @Column(nullable = false, length = 20)
    private String channel; // SMS / EMAIL

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private LocalDateTime sentAt;
}
