package lk.icbt.dentalclinic.service.pattern;

import java.time.LocalDate;

/**
 * FACTORY METHOD PATTERN - "product" interface.
 * Every report type (daily appointments, revenue, dentist workload) implements this
 * common contract so {@code ReportService} can request and render any report
 * uniformly, regardless of how each one queries and shapes its data internally.
 */
public interface Report {
    ReportType type();
    ReportResult generate(LocalDate from, LocalDate to);
}
