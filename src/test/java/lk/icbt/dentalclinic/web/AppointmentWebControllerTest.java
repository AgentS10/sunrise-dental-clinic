package lk.icbt.dentalclinic.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc tests for the staff-facing web UI, covering the "user friendly interfaces
 * ... with proper validation mechanisms" requirement from the brief.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AppointmentWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registrationForm_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/appointments/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "reception", roles = {"RECEPTIONIST"})
    void registrationForm_isVisibleToAuthenticatedStaff() throws Exception {
        mockMvc.perform(get("/appointments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-form"))
                .andExpect(model().attributeExists("dentists", "treatmentTypes", "request"));
    }

    @Test
    @WithMockUser(username = "reception", roles = {"RECEPTIONIST"})
    void submittingBlankForm_reDisplaysFormWithValidationErrors() throws Exception {
        mockMvc.perform(post("/appointments").with(csrf())
                        .param("patientName", "")
                        .param("address", "")
                        .param("contactNumber", "123") // invalid: too short, wrong pattern
                        .param("appointmentDate", "")
                        .param("appointmentTime", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-form"))
                .andExpect(model().attributeHasFieldErrors("request",
                        "patientName", "address", "contactNumber", "dentistId", "treatmentTypeId"));
    }

    @Test
    @WithMockUser(username = "reception", roles = {"RECEPTIONIST"})
    void helpPage_isReachable() throws Exception {
        mockMvc.perform(get("/help"))
                .andExpect(status().isOk())
                .andExpect(view().name("help"));
    }

    @Test
    void loginPage_isPubliclyAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "reception", roles = {"RECEPTIONIST"})
    void reportsPage_isForbiddenForNonAdminStaff() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void reportsPage_isAccessibleToAdmin() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports"));
    }
}
