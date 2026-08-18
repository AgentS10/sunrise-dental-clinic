package lk.icbt.dentalclinic.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A system-generated, staff-facing notification (distinct from {@link Notification},
 * which is the patient-facing SMS simulation). Populated by StaffNotificationObserver
 * whenever a clinically-relevant event occurs (an appointment is registered or
 * cancelled), so every logged-in staff member sees a live activity feed via the
 * notification bell - mirroring how CareStack and Dental Intelligence surface
 * "instant notifications on patient actions" directly in the staff dashboard.
 * <p>
 * Assumption: read/unread state is not tracked per-user (that would need a join
 * table keyed by staff account). Instead the bell shows the most recent N events
 * clinic-wide, which is sufficient for a small front-desk team sharing one screen
 * and keeps the feature within scope - true per-user read tracking is noted as a
 * natural next step in the report.
 */
@Entity
@Table(name = "staff_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String message;

    @Column(length = 20)
    private String appointmentNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
