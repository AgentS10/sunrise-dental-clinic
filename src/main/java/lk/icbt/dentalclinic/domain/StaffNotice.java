package lk.icbt.dentalclinic.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * An internal noticeboard post used by staff to communicate with each other
 * (e.g. "Dr. Perera is on leave tomorrow", "Front desk printer needs paper").
 * Addresses the practical question of how receptionists and admins coordinate
 * day-to-day, the same way iDental Soft and Dental Intelligence provide an
 * internal staff communication/coordination log alongside patient-facing
 * messaging (see the report's comparative analysis, Section 3.9).
 */
@Entity
@Table(name = "staff_notices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private boolean urgent;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
