package hyperskill.evgentrigub.antifraud.models.user;

import hyperskill.evgentrigub.antifraud.models.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto implements Comparable<UserResponseDto> {
    private long id;
    private String name;
    private String username;
    private String role;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.role = user.getRole().toString();
    }

    @Override
    public int compareTo(UserResponseDto other) {
        return Long.compare(this.id, other.id);
    }
}
