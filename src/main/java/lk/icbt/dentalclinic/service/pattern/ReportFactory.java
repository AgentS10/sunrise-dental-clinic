package lk.icbt.dentalclinic.service.pattern;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * FACTORY METHOD PATTERN - "creator". Hides which concrete {@link Report}
 * implementation is instantiated behind a single {@link #createReport(ReportType)}
 * call. {@code ReportWebController} / {@code ReportApiController} depend only on
 * this factory and the {@link Report} interface - adding a new report type
 * (e.g. "no-show report") never requires changing calling code.
 */
@Component
public class ReportFactory {

    private final Map<ReportType, Report> reportsByType;

    public ReportFactory(List<Report> reports) {
        this.reportsByType = reports.stream()
                .collect(Collectors.toMap(Report::type, Function.identity()));
    }

    public Report createReport(ReportType type) {
        Report report = reportsByType.get(type);
        if (report == null) {
            throw new IllegalArgumentException("No report registered for type " + type);
        }
        return report;
    }

    public List<ReportType> availableTypes() {
        return List.copyOf(reportsByType.keySet());
    }
}
