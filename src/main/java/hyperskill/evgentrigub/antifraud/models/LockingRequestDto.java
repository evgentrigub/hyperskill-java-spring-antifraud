package hyperskill.evgentrigub.antifraud.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LockingRequestDto {

    @NotBlank String username;
    @Pattern(regexp = "LOCK|UNLOCK") String operation;
}
