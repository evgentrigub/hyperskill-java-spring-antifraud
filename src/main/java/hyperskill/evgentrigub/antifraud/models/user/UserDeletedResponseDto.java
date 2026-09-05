package hyperskill.evgentrigub.antifraud.models.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletedResponseDto {
    private String username;
    private String status;
}
