package lk.icbt.dentalclinic.repository;

import lk.icbt.dentalclinic.domain.StaffNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffNotificationRepository extends JpaRepository<StaffNotification, Long> {
    List<StaffNotification> findAllByOrderByCreatedAtDesc(Pageable pageable);
    long countByCreatedAtAfter(java.time.LocalDateTime since);
}
