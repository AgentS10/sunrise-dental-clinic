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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SettingsWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "reception", roles = {"RECEPTIONIST"})
    void settingsPage_isForbiddenForNonAdminStaff() throws Exception {
        mockMvc.perform(get("/settings"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void settingsPage_isAccessibleToAdmin() throws Exception {
        mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings"))
                .andExpect(model().attributeExists("dentists", "treatmentTypes", "dentistRequest", "treatmentRequest"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addDentist_withInvalidContactNumber_reDisplaysFormWithError() throws Exception {
        mockMvc.perform(post("/settings/dentists").with(csrf())
                        .param("name", "Dr. Test")
                        .param("specialization", "General")
                        .param("contactNumber", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings"))
                .andExpect(model().attributeHasFieldErrors("dentistRequest", "contactNumber"));
    }
}
