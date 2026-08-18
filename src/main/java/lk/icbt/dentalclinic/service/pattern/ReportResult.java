package lk.icbt.dentalclinic.service.pattern;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The output "product" created by a {@link Report} (Factory Method pattern).
 */
public record ReportResult(
        String title,
        List<String> headers,
        List<List<String>> rows,
        String summary,
        LocalDateTime generatedAt
) {
}
