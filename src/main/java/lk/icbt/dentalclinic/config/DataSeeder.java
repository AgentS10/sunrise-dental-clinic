package lk.icbt.dentalclinic.config;

import lk.icbt.dentalclinic.domain.*;
import lk.icbt.dentalclinic.repository.*;
import lk.icbt.dentalclinic.service.pattern.AppointmentNumberGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds first-run demo data (two staff accounts, dentists, treatment types) and
 * initialises the {@link AppointmentNumberGenerator} Singleton from persisted
 * appointment count, so numbering is correct even after a restart.
 * Idempotent: only inserts when the relevant table is empty.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentTypeRepository treatmentTypeRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, DentistRepository dentistRepository,
                       TreatmentTypeRepository treatmentTypeRepository,
                       AppointmentRepository appointmentRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentTypeRepository = treatmentTypeRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        AppointmentNumberGenerator.getInstance().initialise(appointmentRepository.count());

        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("Admin@123"))
                    .fullName("Clinic Administrator")
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build());

            userRepository.save(User.builder()
                    .username("reception")
                    .password(passwordEncoder.encode("Reception@123"))
                    .fullName("Front Desk Receptionist")
                    .role(Role.RECEPTIONIST)
                    .enabled(true)
                    .build());
        }

        if (dentistRepository.count() == 0) {
            dentistRepository.save(Dentist.builder()
                    .name("Dr. Nimal Perera").specialization("General Dentistry")
                    .contactNumber("0771000001").active(true).build());
            dentistRepository.save(Dentist.builder()
                    .name("Dr. Kavya Fernando").specialization("Orthodontics")
                    .contactNumber("0771000002").active(true).build());
            dentistRepository.save(Dentist.builder()
                    .name("Dr. Ruwan Silva").specialization("Oral Surgery")
                    .contactNumber("0771000003").active(true).build());
        }

        if (treatmentTypeRepository.count() == 0) {
            treatmentTypeRepository.save(TreatmentType.builder()
                    .name("Scaling & Polishing").description("Routine cleaning")
                    .fee(new BigDecimal("3500.00")).estimatedDurationMinutes(30).build());
            treatmentTypeRepository.save(TreatmentType.builder()
                    .name("Tooth Filling").description("Cavity restoration")
                    .fee(new BigDecimal("5000.00")).estimatedDurationMinutes(45).build());
            treatmentTypeRepository.save(TreatmentType.builder()
                    .name("Tooth Extraction").description("Simple extraction")
                    .fee(new BigDecimal("6000.00")).estimatedDurationMinutes(30).build());
            treatmentTypeRepository.save(TreatmentType.builder()
                    .name("Root Canal Treatment").description("Endodontic treatment")
                    .fee(new BigDecimal("15000.00")).estimatedDurationMinutes(90).build());
            treatmentTypeRepository.save(TreatmentType.builder()
                    .name("Braces Consultation").description("Orthodontic assessment")
                    .fee(new BigDecimal("4000.00")).estimatedDurationMinutes(30).build());
            treatmentTypeRepository.save(TreatmentType.builder()
                    .name("Teeth Whitening").description("Cosmetic whitening")
                    .fee(new BigDecimal("12000.00")).estimatedDurationMinutes(60).build());
        }
    }
}
