package lk.icbt.dentalclinic.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dentists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dentist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String specialization;

    @Column(nullable = false, length = 20)
    private String contactNumber;

    @Column(nullable = false)
    private boolean active;
}
