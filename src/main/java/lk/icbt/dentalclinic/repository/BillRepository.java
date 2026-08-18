package lk.icbt.dentalclinic.repository;

import lk.icbt.dentalclinic.domain.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByAppointment_AppointmentNumber(String appointmentNumber);
    List<Bill> findByGeneratedAtBetween(LocalDateTime from, LocalDateTime to);
}
