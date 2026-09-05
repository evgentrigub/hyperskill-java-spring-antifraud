package hyperskill.evgentrigub.antifraud.models.transaction;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDto {
    @NotNull
    @Min(1)
    Long amount;

    @NotBlank
    @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")
    String IP;

    @NotBlank
    @Pattern(regexp = "\\b(?:\\d[ -]*?){13,16}\\b")
    String number;

    private String region;
    private String date;
}