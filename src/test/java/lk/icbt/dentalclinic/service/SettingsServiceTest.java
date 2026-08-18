package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.domain.Dentist;
import lk.icbt.dentalclinic.dto.NewDentistRequest;
import lk.icbt.dentalclinic.dto.NewTreatmentTypeRequest;
import lk.icbt.dentalclinic.repository.DentistRepository;
import lk.icbt.dentalclinic.repository.TreatmentTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {

    @Mock private DentistRepository dentistRepository;
    @Mock private TreatmentTypeRepository treatmentTypeRepository;

    private SettingsService settingsService;

    @BeforeEach
    void setUp() {
        settingsService = new SettingsService(dentistRepository, treatmentTypeRepository);
    }

    @Test
    void addDentist_savesActiveDentist() {
        NewDentistRequest request = new NewDentistRequest();
        request.setName("Dr. New Hire");
        request.setSpecialization("Orthodontics");
        request.setContactNumber("0779998888");
        when(dentistRepository.save(any(Dentist.class))).thenAnswer(inv -> inv.getArgument(0));

        Dentist result = settingsService.addDentist(request);

        assertThat(result.isActive()).isTrue();
        assertThat(result.getName()).isEqualTo("Dr. New Hire");
    }

    @Test
    void toggleDentistActive_flipsActiveFlag() {
        Dentist dentist = Dentist.builder().id(1L).name("Dr. X").active(true).build();
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(dentistRepository.save(any(Dentist.class))).thenAnswer(inv -> inv.getArgument(0));

        Dentist result = settingsService.toggleDentistActive(1L);

        assertThat(result.isActive()).isFalse();
    }

    @Test
    void addTreatmentType_savesWithSuppliedFee() {
        NewTreatmentTypeRequest request = new NewTreatmentTypeRequest();
        request.setName("Dental Implants");
        request.setFee(new BigDecimal("45000.00"));
        request.setEstimatedDurationMinutes(120);
        when(treatmentTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = settingsService.addTreatmentType(request);

        assertThat(result.getFee()).isEqualByComparingTo("45000.00");
        assertThat(result.getEstimatedDurationMinutes()).isEqualTo(120);
    }
}
