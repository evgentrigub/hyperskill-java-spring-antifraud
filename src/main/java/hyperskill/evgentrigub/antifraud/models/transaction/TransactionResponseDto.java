package hyperskill.evgentrigub.antifraud.models.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDto {
    private TransactionStatus result;
    private String info;
}