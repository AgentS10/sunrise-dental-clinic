package lk.icbt.dentalclinic.service.pattern;

public enum ReportType {
    DAILY_APPOINTMENTS("Daily Appointments Report"),
    REVENUE("Revenue Report"),
    DENTIST_WORKLOAD("Dentist Workload Report");

    private final String displayName;

    ReportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
