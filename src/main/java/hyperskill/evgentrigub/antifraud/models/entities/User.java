package hyperskill.evgentrigub.antifraud.models.entities;

import hyperskill.evgentrigub.antifraud.models.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NullMarked
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean locked;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        if (this.role == Role.ADMINISTRATOR) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
        } else if (this.role == Role.SUPPORT) {
            authorities.add(new SimpleGrantedAuthority("ROLE_SUPPORT"));
        } else if (this.role == Role.MERCHANT) {
            authorities.add(new SimpleGrantedAuthority("ROLE_MERCHANT"));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonLocked() {
        return locked;
    }
}
