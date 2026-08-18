package lk.icbt.dentalclinic.api;

import lk.icbt.dentalclinic.domain.Dentist;
import lk.icbt.dentalclinic.domain.TreatmentType;
import lk.icbt.dentalclinic.service.ClinicFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LookupApiController {

    private final ClinicFacade clinicFacade;

    public LookupApiController(ClinicFacade clinicFacade) {
        this.clinicFacade = clinicFacade;
    }

    @GetMapping("/dentists")
    public List<Dentist> dentists() {
        return clinicFacade.activeDentists();
    }

    @GetMapping("/treatment-types")
    public List<TreatmentType> treatmentTypes() {
        return clinicFacade.treatmentTypes();
    }
}
