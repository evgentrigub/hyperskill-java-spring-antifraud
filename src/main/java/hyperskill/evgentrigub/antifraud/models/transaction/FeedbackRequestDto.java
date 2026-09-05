package hyperskill.evgentrigub.antifraud.models.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FeedbackRequestDto {
    @NotNull
    private Long transactionId;

    @NotBlank
    private String feedback;
}
