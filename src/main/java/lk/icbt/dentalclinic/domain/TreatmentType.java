package lk.icbt.dentalclinic.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * A treatment offered by the clinic, with its base fee. Used by the billing strategy
 * to calculate the total treatment cost (Functionality 4: Calculate and Print Bill).
 */
@Entity
@Table(name = "treatment_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fee;

    @Column(nullable = false)
    private int estimatedDurationMinutes;
}
