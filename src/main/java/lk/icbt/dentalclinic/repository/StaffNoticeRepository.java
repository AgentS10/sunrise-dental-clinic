package lk.icbt.dentalclinic.repository;

import lk.icbt.dentalclinic.domain.StaffNotice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StaffNoticeRepository extends JpaRepository<StaffNotice, Long> {

    // join fetch: the author is always displayed alongside the notice, so this
    // avoids the LazyInitializationException that bit ClinicFacade earlier
    // (Section 3.3.4) - author.fullName is read by the view after the
    // transaction closes, so it must already be initialised here.
    @Query("select n from StaffNotice n join fetch n.author order by n.createdAt desc")
    List<StaffNotice> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
