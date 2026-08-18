package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.service.pattern.Report;
import lk.icbt.dentalclinic.service.pattern.ReportFactory;
import lk.icbt.dentalclinic.service.pattern.ReportResult;
import lk.icbt.dentalclinic.service.pattern.ReportType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * "Come up with a suitable set of reports which add more value to your system"
 * (Task B). Thin wrapper around {@link ReportFactory} so controllers never need
 * to know the Factory Method pattern is involved underneath.
 */
@Service
@Transactional(readOnly = true)
public class ReportService {

    private final ReportFactory reportFactory;

    public ReportService(ReportFactory reportFactory) {
        this.reportFactory = reportFactory;
    }

    public ReportResult generate(ReportType type, LocalDate from, LocalDate to) {
        Report report = reportFactory.createReport(type);
        return report.generate(from, to);
    }

    public List<ReportType> availableReportTypes() {
        return reportFactory.availableTypes();
    }
}
