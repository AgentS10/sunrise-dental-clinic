package lk.icbt.dentalclinic.repository;

import lk.icbt.dentalclinic.domain.TreatmentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentTypeRepository extends JpaRepository<TreatmentType, Long> {
}
