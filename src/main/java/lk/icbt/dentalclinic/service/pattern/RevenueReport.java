package lk.icbt.dentalclinic.service.pattern;

import lk.icbt.dentalclinic.domain.Bill;
import lk.icbt.dentalclinic.repository.BillRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class RevenueReport implements Report {

    private final BillRepository billRepository;

    public RevenueReport(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    public ReportType type() {
        return ReportType.REVENUE;
    }

    @Override
    public ReportResult generate(LocalDate from, LocalDate to) {
        List<Bill> bills = billRepository.findByGeneratedAtBetween(
                from.atStartOfDay(), to.atTime(LocalTime.MAX));

        List<List<String>> rows = bills.stream()
                .map(b -> List.of(
                        b.getBillNumber(),
                        b.getAppointment().getAppointmentNumber(),
                        b.getAppointment().getPatient().getName(),
                        b.getPricingStrategyUsed(),
                        b.getTotalAmount().toPlainString()))
                .toList();

        BigDecimal total = bills.stream()
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReportResult(
                "Revenue Report (" + from + " to " + to + ")",
                List.of("Bill No.", "Appt No.", "Patient", "Pricing Strategy", "Amount (LKR)"),
                rows,
                "Total revenue: LKR " + total.toPlainString() + " across " + bills.size() + " bill(s)",
                LocalDateTime.now());
    }
}
