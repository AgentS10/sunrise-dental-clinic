package lk.icbt.dentalclinic.dto;

import java.time.LocalDateTime;

public record ApiError(int status, String error, String message, LocalDateTime timestamp) {
    public static ApiError of(int status, String error, String message) {
        return new ApiError(status, error, message, LocalDateTime.now());
    }
}
