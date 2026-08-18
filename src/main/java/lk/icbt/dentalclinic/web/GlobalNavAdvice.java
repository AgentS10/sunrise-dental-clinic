package lk.icbt.dentalclinic.web;

import lk.icbt.dentalclinic.domain.StaffNotification;
import lk.icbt.dentalclinic.repository.StaffNotificationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Populates the notification bell (fragments/nav.html) on every staff-facing page,
 * so "what has happened since I last looked" is visible everywhere, not just on
 * one dashboard widget. Scoped to the web package only - the JSON API has its own
 * concerns and should never render HTML fragments.
 */
@ControllerAdvice(basePackages = "lk.icbt.dentalclinic.web")
public class GlobalNavAdvice {

    private static final int BELL_LIMIT = 8;

    private final StaffNotificationRepository staffNotificationRepository;

    public GlobalNavAdvice(StaffNotificationRepository staffNotificationRepository) {
        this.staffNotificationRepository = staffNotificationRepository;
    }

    @ModelAttribute("recentStaffNotifications")
    public List<StaffNotification> recentStaffNotifications(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return Collections.emptyList();
        }
        return staffNotificationRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, BELL_LIMIT));
    }

    @ModelAttribute("staffNotificationCount")
    public long staffNotificationCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return 0;
        }
        return staffNotificationRepository.countByCreatedAtAfter(LocalDateTime.now().minusHours(24));
    }
}
