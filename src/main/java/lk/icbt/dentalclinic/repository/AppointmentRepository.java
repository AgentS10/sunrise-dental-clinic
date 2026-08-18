package lk.icbt.dentalclinic.repository;

import lk.icbt.dentalclinic.domain.Appointment;
import lk.icbt.dentalclinic.domain.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);

    List<Appointment> findByAppointmentDateOrderByAppointmentTimeAsc(LocalDate date);

    long countByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAscAppointmentTimeAsc(
            LocalDate from, LocalDate to);

    long countByAppointmentNumberStartingWith(String prefix);
}
