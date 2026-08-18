package lk.icbt.dentalclinic.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A registered patient. A patient is registered once and may have many appointments
 * over time (1..* association with Appointment) - this normalises the flat field list
 * given in the brief (name/address/contact per appointment) so returning patients are
 * not re-entered on every visit. Documented as a design assumption in the report.
 */
@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false, length = 20)
    private String contactNumber;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();
}
