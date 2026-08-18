package lk.icbt.dentalclinic.service.pattern;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReportFactoryTest {

    private static final class StubReport implements Report {
        private final ReportType type;
        StubReport(ReportType type) { this.type = type; }
        @Override public ReportType type() { return type; }
        @Override public ReportResult generate(LocalDate from, LocalDate to) {
            return new ReportResult(type.getDisplayName(), List.of(), List.of(), "stub", LocalDateTime.now());
        }
    }

    @Test
    void createReport_returnsTheReportMatchingTheRequestedType() {
        ReportFactory factory = new ReportFactory(List.of(
                new StubReport(ReportType.DAILY_APPOINTMENTS), new StubReport(ReportType.REVENUE)));

        Report report = factory.createReport(ReportType.REVENUE);

        assertThat(report.type()).isEqualTo(ReportType.REVENUE);
    }

    @Test
    void createReport_throwsForAnUnregisteredType() {
        ReportFactory factory = new ReportFactory(List.of(new StubReport(ReportType.DAILY_APPOINTMENTS)));

        assertThatThrownBy(() -> factory.createReport(ReportType.DENTIST_WORKLOAD))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
