package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.domain.StaffNotice;
import lk.icbt.dentalclinic.domain.User;
import lk.icbt.dentalclinic.repository.StaffNoticeRepository;
import lk.icbt.dentalclinic.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * How staff communicate with each other (as opposed to AppointmentEventPublisher's
 * observers, which react to system events). A lightweight internal noticeboard -
 * receptionists and admins post short notes ("Dr. Perera is on leave tomorrow",
 * "front desk printer needs paper") that every logged-in staff member sees on the
 * main menu. Modelled after the "organised communication between staff and office
 * groups for quick coordination and task delegation" feature found in comparable
 * commercial systems (see the report's comparative analysis).
 */
@Service
@Transactional
public class StaffNoticeService {

    private static final int RECENT_LIMIT = 20;

    private final StaffNoticeRepository staffNoticeRepository;
    private final UserRepository userRepository;

    public StaffNoticeService(StaffNoticeRepository staffNoticeRepository, UserRepository userRepository) {
        this.staffNoticeRepository = staffNoticeRepository;
        this.userRepository = userRepository;
    }

    public StaffNotice post(String authorUsername, String message, boolean urgent) {
        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new IllegalStateException("Unknown user: " + authorUsername));

        StaffNotice notice = StaffNotice.builder()
                .author(author)
                .message(message)
                .urgent(urgent)
                .createdAt(LocalDateTime.now())
                .build();

        return staffNoticeRepository.save(notice);
    }

    @Transactional(readOnly = true)
    public List<StaffNotice> recent() {
        return staffNoticeRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, RECENT_LIMIT));
    }

    public void delete(Long id) {
        staffNoticeRepository.deleteById(id);
    }
}
