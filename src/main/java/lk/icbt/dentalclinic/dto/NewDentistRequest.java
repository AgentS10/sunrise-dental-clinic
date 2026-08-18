package lk.icbt.dentalclinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewDentistRequest {

    @NotBlank(message = "Dentist name is required")
    private String name;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^0\\d{9}$", message = "Contact number must be a valid 10-digit number starting with 0")
    private String contactNumber;
}
