package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.Appointment;
import lk.icbt.dentalclinic.repository.AppointmentRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DailyAppointmentsReport implements Report {

    private final AppointmentRepository appointmentRepository;

    public DailyAppointmentsReport(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public ReportType type() {
        return ReportType.DAILY_APPOINTMENTS;
    }

    @Override
    public ReportResult generate(LocalDate from, LocalDate to) {
        List<Appointment> appointments =
                appointmentRepository.findByAppointmentDateBetweenOrderByAppointmentDateAscAppointmentTimeAsc(from, to);

        List<List<String>> rows = appointments.stream()
                .map(a -> List.of(
                        a.getAppointmentNumber(),
                        a.getPatient().getName(),
                        a.getDentist().getName(),
                        a.getTreatmentType().getName(),
                        a.getAppointmentDate().toString(),
                        a.getAppointmentTime().toString(),
                        a.getStatus().toString()))
                .toList();

        return new ReportResult(
                "Appointments Report (" + from + " to " + to + ")",
                List.of("Appt No.", "Patient", "Dentist", "Treatment", "Date", "Time", "Status"),
                rows,
                appointments.size() + " appointment(s) found",
                LocalDateTime.now());
    }
}
