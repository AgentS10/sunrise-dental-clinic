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
class StaffNoticeWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void noticeboard_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/notices"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "reception", roles = {"RECEPTIONIST"})
    void postingBlankNotice_reDisplaysWithValidationError() throws Exception {
        mockMvc.perform(post("/notices").with(csrf()).param("message", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("notices"))
                .andExpect(model().attributeHasFieldErrors("request", "message"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void postingValidNotice_redirectsToNoticeboard() throws Exception {
        mockMvc.perform(post("/notices").with(csrf()).param("message", "Front desk printer needs paper"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notices"));
    }

    @Test
    @WithMockUser(username = "reception", roles = {"RECEPTIONIST"})
    void deletingNotice_isForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(post("/notices/1/delete").with(csrf()))
                .andExpect(status().isForbidden());
    }
}
