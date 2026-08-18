package lk.icbt.dentalclinic.api;

import lk.icbt.dentalclinic.dto.ApiError;
import lk.icbt.dentalclinic.exception.AppointmentNotFoundException;
import lk.icbt.dentalclinic.exception.DoubleBookingException;
import lk.icbt.dentalclinic.exception.InvalidAppointmentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/** Turns domain/validation exceptions into consistent JSON error responses for the REST API. */
@RestControllerAdvice(basePackages = "lk.icbt.dentalclinic.api")
public class ApiExceptionHandler {

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(AppointmentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(DoubleBookingException.class)
    public ResponseEntity<ApiError> handleDoubleBooking(DoubleBookingException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(409, "Conflict", ex.getMessage()));
    }

    @ExceptionHandler(InvalidAppointmentException.class)
    public ResponseEntity<ApiError> handleInvalid(InvalidAppointmentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(400, "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(400, "Validation Failed", message));
    }
}
