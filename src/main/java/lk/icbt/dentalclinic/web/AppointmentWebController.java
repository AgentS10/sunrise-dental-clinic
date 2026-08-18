package lk.icbt.dentalclinic.web;

import jakarta.validation.Valid;
import lk.icbt.dentalclinic.dto.AppointmentResponse;
import lk.icbt.dentalclinic.dto.BillResponse;
import lk.icbt.dentalclinic.dto.RegisterAppointmentRequest;
import lk.icbt.dentalclinic.exception.AppointmentNotFoundException;
import lk.icbt.dentalclinic.exception.DoubleBookingException;
import lk.icbt.dentalclinic.exception.InvalidAppointmentException;
import lk.icbt.dentalclinic.service.ClinicFacade;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Presentation-layer controller for Functionalities 2, 3 and 4 (register, display,
 * bill). Talks only to {@link ClinicFacade} (Facade pattern) - it has no knowledge
 * of repositories, billing strategies, or the appointment number generator.
 */
@Controller
@RequestMapping("/appointments")
public class AppointmentWebController {

    private final ClinicFacade clinicFacade;

    public AppointmentWebController(ClinicFacade clinicFacade) {
        this.clinicFacade = clinicFacade;
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        if (!model.containsAttribute("request")) {
            model.addAttribute("request", new RegisterAppointmentRequest());
        }
        addLookups(model);
        return "appointment-form";
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("request") RegisterAppointmentRequest request,
                          BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addLookups(model);
            return "appointment-form";
        }
        try {
            AppointmentResponse response = clinicFacade.registerAppointment(request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Appointment " + response.appointmentNumber() + " registered successfully for "
                            + response.patientName() + ". An SMS confirmation has been simulated.");
            return "redirect:/appointments/" + response.appointmentNumber();
        } catch (DoubleBookingException | InvalidAppointmentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            addLookups(model);
            return "appointment-form";
        }
    }

    @GetMapping("/search")
    public String searchForm() {
        return "appointment-search";
    }

    @PostMapping("/search")
    public String search(@RequestParam String appointmentNumber, Model model) {
        if (!StringUtils.hasText(appointmentNumber)) {
            model.addAttribute("errorMessage", "Please enter an appointment number.");
            return "appointment-search";
        }
        return "redirect:/appointments/" + appointmentNumber.trim().toUpperCase();
    }

    @GetMapping("/{appointmentNumber}")
    public String detail(@PathVariable String appointmentNumber, Model model) {
        try {
            model.addAttribute("appointment", clinicFacade.findAppointment(appointmentNumber));
            return "appointment-detail";
        } catch (AppointmentNotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "appointment-search";
        }
    }

    @PostMapping("/{appointmentNumber}/bill")
    public String generateBill(@PathVariable String appointmentNumber) {
        clinicFacade.billFor(appointmentNumber); // creates the bill on first call
        return "redirect:/appointments/" + appointmentNumber + "/bill";
    }

    @GetMapping("/{appointmentNumber}/bill")
    public String viewBill(@PathVariable String appointmentNumber, Model model) {
        AppointmentResponse appointment = clinicFacade.findAppointment(appointmentNumber);
        BillResponse bill = clinicFacade.billFor(appointmentNumber);
        model.addAttribute("appointment", appointment);
        model.addAttribute("bill", bill);
        return "bill";
    }

    private void addLookups(Model model) {
        model.addAttribute("dentists", clinicFacade.activeDentists());
        model.addAttribute("treatmentTypes", clinicFacade.treatmentTypes());
    }
}
