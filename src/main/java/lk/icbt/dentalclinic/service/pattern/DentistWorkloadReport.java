package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.Appointment;
import lk.icbt.dentalclinic.domain.Dentist;
import lk.icbt.dentalclinic.repository.AppointmentRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DentistWorkloadReport implements Report {

    private final AppointmentRepository appointmentRepository;

    public DentistWorkloadReport(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public ReportType type() {
        return ReportType.DENTIST_WORKLOAD;
    }

    @Override
    public ReportResult generate(LocalDate from, LocalDate to) {
        List<Appointment> appointments =
                appointmentRepository.findByAppointmentDateBetweenOrderByAppointmentDateAscAppointmentTimeAsc(from, to);

        Map<Dentist, Long> countByDentist = appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getDentist, Collectors.counting()));

        List<List<String>> rows = countByDentist.entrySet().stream()
                .sorted(Comparator.<Map.Entry<Dentist, Long>>comparingLong(Map.Entry::getValue).reversed())
                .map(e -> List.of(
                        e.getKey().getName(),
                        e.getKey().getSpecialization(),
                        String.valueOf(e.getValue())))
                .toList();

        return new ReportResult(
                "Dentist Workload Report (" + from + " to " + to + ")",
                List.of("Dentist", "Specialization", "Appointments"),
                rows,
                countByDentist.size() + " dentist(s) had appointments in this period",
                LocalDateTime.now());
    }
}
