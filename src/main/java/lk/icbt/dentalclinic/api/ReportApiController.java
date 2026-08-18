package lk.icbt.dentalclinic.api;

import lk.icbt.dentalclinic.service.ClinicFacade;
import lk.icbt.dentalclinic.service.pattern.ReportResult;
import lk.icbt.dentalclinic.service.pattern.ReportType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportApiController {

    private final ClinicFacade clinicFacade;

    public ReportApiController(ClinicFacade clinicFacade) {
        this.clinicFacade = clinicFacade;
    }

    @GetMapping
    public List<ReportType> availableTypes() {
        return clinicFacade.availableReportTypes();
    }

    @GetMapping("/{type}")
    public ReportResult generate(@PathVariable ReportType type,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return clinicFacade.report(type, from, to);
    }
}
