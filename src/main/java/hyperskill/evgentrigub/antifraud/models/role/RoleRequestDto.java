package hyperskill.evgentrigub.antifraud.models.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleRequestDto {

    @NotBlank
    String username;

    @NotBlank
    String role;
}
