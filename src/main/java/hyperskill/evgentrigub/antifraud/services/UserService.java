package hyperskill.evgentrigub.antifraud.services;

import hyperskill.evgentrigub.antifraud.models.*;
import hyperskill.evgentrigub.antifraud.models.entities.User;
import hyperskill.evgentrigub.antifraud.models.role.Role;
import hyperskill.evgentrigub.antifraud.models.role.RoleRequestDto;
import hyperskill.evgentrigub.antifraud.models.user.UserRequestDto;
import hyperskill.evgentrigub.antifraud.models.user.UserResponseDto;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@NullMarked
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Optional<User> addNewUser(UserRequestDto userDto) {
        Optional<User> userOptional = userRepository.findByUsername(userDto.getUsername());
        if (userOptional.isPresent()) {
            return Optional.empty();
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        if (userRepository.count() == 0) {
            user.setRole(Role.ADMINISTRATOR);
            user.setLocked(true);
        } else {
            user.setRole(Role.MERCHANT);
            user.setLocked(false);
        }

        return Optional.of(userRepository.save(user));
    }

    public List<UserResponseDto> getAllUsersAscending() {
        List<UserResponseDto> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> users.add(new UserResponseDto(user)));
        users.sort(Comparator.naturalOrder());
        return users;
    }

    @Transactional
    public boolean deleteUser(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        if (byUsername.isEmpty()) {
            return false;
        }
        userRepository.delete(byUsername.get());
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with provided username not found."));
    }

    public ResponseEntity<Object> setRole(RoleRequestDto roleDto) {
        Optional<User> userOptional = userRepository.findByUsername(roleDto.getUsername());
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        if (user.getRole().equals(Role.valueOf(roleDto.getRole().toUpperCase()))) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        user.setRole(Role.valueOf(roleDto.getRole().toUpperCase()));
        userRepository.save(user);
        return new ResponseEntity<>(new UserResponseDto(user), HttpStatus.OK);
    }

    public StatusResponseDto locking(LockingRequestDto lockingDto) {
        Optional<User> userOptional = userRepository.findByUsername(lockingDto.getUsername());
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        if (user.getRole().equals(Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        boolean nonLocked = switch (lockingDto.getOperation().toLowerCase()) {
            case "lock" -> false;
            case "unlock" -> true;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        };
        user.setLocked(nonLocked);
        userRepository.save(user);
        String operation = nonLocked ? "unlocked" : "locked";
        return new StatusResponseDto("User " + user.getUsername() + " " + operation + "!");
    }
}
