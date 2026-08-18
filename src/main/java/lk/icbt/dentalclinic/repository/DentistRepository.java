package lk.icbt.dentalclinic.repository;

import lk.icbt.dentalclinic.domain.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DentistRepository extends JpaRepository<Dentist, Long> {
    List<Dentist> findByActiveTrue();
}
