package lk.icbt.dentalclinic.exception;

/**
 * Raised when a dentist is already booked for the requested date and time.
 * Directly addresses the "double bookings" problem described in the brief's scenario.
 */
public class DoubleBookingException extends RuntimeException {
    public DoubleBookingException(String dentistName, String date, String time) {
        super(dentistName + " already has an appointment on " + date + " at " + time
                + ". Please choose a different time.");
    }
}
