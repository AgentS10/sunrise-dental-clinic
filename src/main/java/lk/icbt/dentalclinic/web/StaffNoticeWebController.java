package lk.icbt.dentalclinic.web;

import jakarta.validation.Valid;
import lk.icbt.dentalclinic.dto.PostNoticeRequest;
import lk.icbt.dentalclinic.service.StaffNoticeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/** Functionality added post-review: an internal noticeboard for staff-to-staff communication. */
@Controller
@RequestMapping("/notices")
public class StaffNoticeWebController {

    private final StaffNoticeService staffNoticeService;

    public StaffNoticeWebController(StaffNoticeService staffNoticeService) {
        this.staffNoticeService = staffNoticeService;
    }

    @GetMapping
    public String list(Model model) {
        if (!model.containsAttribute("request")) {
            model.addAttribute("request", new PostNoticeRequest());
        }
        model.addAttribute("notices", staffNoticeService.recent());
        return "notices";
    }

    @PostMapping
    public String post(@Valid @ModelAttribute("request") PostNoticeRequest request, BindingResult bindingResult,
                        Authentication authentication, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("notices", staffNoticeService.recent());
            return "notices";
        }
        staffNoticeService.post(authentication.getName(), request.getMessage(), request.isUrgent());
        return "redirect:/notices";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        staffNoticeService.delete(id);
        return "redirect:/notices";
    }
}
