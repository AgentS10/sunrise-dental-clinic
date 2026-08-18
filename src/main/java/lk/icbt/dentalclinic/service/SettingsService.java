package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.domain.Dentist;
import lk.icbt.dentalclinic.domain.TreatmentType;
import lk.icbt.dentalclinic.dto.NewDentistRequest;
import lk.icbt.dentalclinic.dto.NewTreatmentTypeRequest;
import lk.icbt.dentalclinic.repository.DentistRepository;
import lk.icbt.dentalclinic.repository.TreatmentTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Clinic configuration management (Settings): dentists and treatment types are
 * reference data seeded once at first run (DataSeeder) but a real clinic adds a
 * new dentist or changes a treatment's fee regularly - this was originally only
 * possible by editing DataSeeder and restarting the application, which is not a
 * realistic operating model. Admin-only, since this data affects every future
 * booking and bill calculated across the whole clinic.
 */
@Service
@Transactional
public class SettingsService {

    private final DentistRepository dentistRepository;
    private final TreatmentTypeRepository treatmentTypeRepository;

    public SettingsService(DentistRepository dentistRepository, TreatmentTypeRepository treatmentTypeRepository) {
        this.dentistRepository = dentistRepository;
        this.treatmentTypeRepository = treatmentTypeRepository;
    }

    @Transactional(readOnly = true)
    public List<Dentist> allDentists() {
        return dentistRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TreatmentType> allTreatmentTypes() {
        return treatmentTypeRepository.findAll();
    }

    public Dentist addDentist(NewDentistRequest request) {
        return dentistRepository.save(Dentist.builder()
                .name(request.getName())
                .specialization(request.getSpecialization())
                .contactNumber(request.getContactNumber())
                .active(true)
                .build());
    }

    public Dentist toggleDentistActive(Long id) {
        Dentist dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No dentist found with id " + id));
        dentist.setActive(!dentist.isActive());
        return dentistRepository.save(dentist);
    }

    public TreatmentType addTreatmentType(NewTreatmentTypeRequest request) {
        return treatmentTypeRepository.save(TreatmentType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .fee(request.getFee())
                .estimatedDurationMinutes(request.getEstimatedDurationMinutes())
                .build());
    }
}
