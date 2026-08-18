package lk.icbt.dentalclinic.repository;

import lk.icbt.dentalclinic.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByContactNumber(String contactNumber);
    List<Patient> findByNameContainingIgnoreCase(String name);
}
