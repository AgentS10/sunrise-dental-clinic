package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.domain.Role;
import lk.icbt.dentalclinic.domain.StaffNotice;
import lk.icbt.dentalclinic.domain.User;
import lk.icbt.dentalclinic.repository.StaffNoticeRepository;
import lk.icbt.dentalclinic.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffNoticeServiceTest {

    @Mock private StaffNoticeRepository staffNoticeRepository;
    @Mock private UserRepository userRepository;

    private StaffNoticeService service;

    @Test
    void post_resolvesAuthorByUsername_andPersistsNotice() {
        service = new StaffNoticeService(staffNoticeRepository, userRepository);
        User admin = User.builder().username("admin").fullName("Clinic Administrator").role(Role.ADMIN).build();
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(staffNoticeRepository.save(any(StaffNotice.class))).thenAnswer(inv -> inv.getArgument(0));

        StaffNotice result = service.post("admin", "Front desk printer needs paper", true);

        ArgumentCaptor<StaffNotice> captor = ArgumentCaptor.forClass(StaffNotice.class);
        verify(staffNoticeRepository).save(captor.capture());
        assertThat(captor.getValue().getAuthor()).isEqualTo(admin);
        assertThat(captor.getValue().getMessage()).isEqualTo("Front desk printer needs paper");
        assertThat(captor.getValue().isUrgent()).isTrue();
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void post_throwsForUnknownUsername() {
        service = new StaffNoticeService(staffNoticeRepository, userRepository);
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.post("ghost", "hello", false))
                .isInstanceOf(IllegalStateException.class);
    }
}
