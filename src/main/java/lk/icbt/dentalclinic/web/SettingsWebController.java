package lk.icbt.dentalclinic.web;

import jakarta.validation.Valid;
import lk.icbt.dentalclinic.dto.NewDentistRequest;
import lk.icbt.dentalclinic.dto.NewTreatmentTypeRequest;
import lk.icbt.dentalclinic.service.SettingsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin-only clinic configuration: manage dentists and treatment types. */
@Controller
@RequestMapping("/settings")
@PreAuthorize("hasRole('ADMIN')")
public class SettingsWebController {

    private final SettingsService settingsService;

    public SettingsWebController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public String view(Model model) {
        addLookups(model);
        if (!model.containsAttribute("dentistRequest")) {
            model.addAttribute("dentistRequest", new NewDentistRequest());
        }
        if (!model.containsAttribute("treatmentRequest")) {
            model.addAttribute("treatmentRequest", new NewTreatmentTypeRequest());
        }
        return "settings";
    }

    @PostMapping("/dentists")
    public String addDentist(@Valid @ModelAttribute("dentistRequest") NewDentistRequest request,
                              BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            addLookups(model);
            model.addAttribute("treatmentRequest", new NewTreatmentTypeRequest());
            model.addAttribute("openTab", "dentists");
            return "settings";
        }
        settingsService.addDentist(request);
        return "redirect:/settings";
    }

    @PostMapping("/dentists/{id}/toggle")
    public String toggleDentist(@PathVariable Long id) {
        settingsService.toggleDentistActive(id);
        return "redirect:/settings";
    }

    @PostMapping("/treatment-types")
    public String addTreatmentType(@Valid @ModelAttribute("treatmentRequest") NewTreatmentTypeRequest request,
                                    BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            addLookups(model);
            model.addAttribute("dentistRequest", new NewDentistRequest());
            model.addAttribute("openTab", "treatments");
            return "settings";
        }
        settingsService.addTreatmentType(request);
        return "redirect:/settings";
    }

    private void addLookups(Model model) {
        model.addAttribute("dentists", settingsService.allDentists());
        model.addAttribute("treatmentTypes", settingsService.allTreatmentTypes());
    }
}
