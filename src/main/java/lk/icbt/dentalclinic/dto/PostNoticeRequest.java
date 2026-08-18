package lk.icbt.dentalclinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostNoticeRequest {

    @NotBlank(message = "Notice message is required")
    @Size(max = 500, message = "Notice message must be at most 500 characters")
    private String message;

    private boolean urgent;
}
