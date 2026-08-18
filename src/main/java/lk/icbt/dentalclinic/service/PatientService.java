package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.domain.Patient;
import lk.icbt.dentalclinic.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Registers a new patient, or - if a patient with the same contact number already
     * exists - reuses that record and refreshes their name/address. This keeps a
     * returning patient's visit history under one record instead of duplicating them
     * on every appointment (see the assumption documented on {@link Patient}).
     */
    public Patient findOrRegister(String name, String address, String contactNumber) {
        return patientRepository.findByContactNumber(contactNumber)
                .map(existing -> {
                    existing.setName(name);
                    existing.setAddress(address);
                    return existing;
                })
                .orElseGet(() -> patientRepository.save(
                        Patient.builder()
                                .name(name)
                                .address(address)
                                .contactNumber(contactNumber)
                                .build()));
    }

    public Patient getById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No patient found with id " + id));
    }

    public List<Patient> search(String query) {
        return patientRepository.findByNameContainingIgnoreCase(query);
    }

    public Optional<Patient> findByContactNumber(String contactNumber) {
        return patientRepository.findByContactNumber(contactNumber);
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }
}
