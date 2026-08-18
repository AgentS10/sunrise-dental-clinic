package lk.icbt.dentalclinic.web;

import lk.icbt.dentalclinic.service.ClinicFacade;
import lk.icbt.dentalclinic.service.pattern.ReportType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/** Reports add-on value required by Task B ("suitable set of reports"). Admin only. */
@Controller
@RequestMapping("/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportWebController {

    private final ClinicFacade clinicFacade;

    public ReportWebController(ClinicFacade clinicFacade) {
        this.clinicFacade = clinicFacade;
    }

    @GetMapping
    public String view(@RequestParam(required = false) ReportType type,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                        Model model) {
        model.addAttribute("reportTypes", clinicFacade.availableReportTypes());

        LocalDate effectiveFrom = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate effectiveTo = to != null ? to : LocalDate.now();
        model.addAttribute("from", effectiveFrom);
        model.addAttribute("to", effectiveTo);

        if (type != null) {
            model.addAttribute("selectedType", type);
            model.addAttribute("result", clinicFacade.report(type, effectiveFrom, effectiveTo));
        }
        return "reports";
    }
}
