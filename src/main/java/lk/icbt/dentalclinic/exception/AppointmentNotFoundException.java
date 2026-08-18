package lk.icbt.dentalclinic.exception;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(String appointmentNumber) {
        super("No appointment found with number: " + appointmentNumber);
    }
}
