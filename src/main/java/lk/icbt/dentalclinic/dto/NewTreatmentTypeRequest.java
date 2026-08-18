package lk.icbt.dentalclinic.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class NewTreatmentTypeRequest {

    @NotBlank(message = "Treatment name is required")
    private String name;

    private String description;

    @NotNull(message = "Fee is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Fee cannot be negative")
    private BigDecimal fee;

    @NotNull(message = "Estimated duration is required")
    @Min(value = 5, message = "Estimated duration must be at least 5 minutes")
    private Integer estimatedDurationMinutes;
}
