package lk.icbt.dentalclinic.repository;

import lk.icbt.dentalclinic.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAppointmentNumber(String appointmentNumber);
}
