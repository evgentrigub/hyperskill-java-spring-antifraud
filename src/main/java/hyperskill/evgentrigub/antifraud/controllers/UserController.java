package hyperskill.evgentrigub.antifraud.controllers;

import hyperskill.evgentrigub.antifraud.exceptions.UserAlreadyExistsException;
import hyperskill.evgentrigub.antifraud.models.LockingRequestDto;
import hyperskill.evgentrigub.antifraud.models.StatusResponseDto;
import hyperskill.evgentrigub.antifraud.models.entities.User;
import hyperskill.evgentrigub.antifraud.models.role.RoleRequestDto;
import hyperskill.evgentrigub.antifraud.models.user.UserDeletedResponseDto;
import hyperskill.evgentrigub.antifraud.models.user.UserRequestDto;
import hyperskill.evgentrigub.antifraud.models.user.UserResponseDto;
import hyperskill.evgentrigub.antifraud.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/auth")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/user")
    public ResponseEntity<UserResponseDto> addUser(@RequestBody @Valid UserRequestDto userRequest) {

        Optional<User> user = userService.addNewUser(userRequest);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        UserResponseDto userResponseDto = new UserResponseDto(user.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersAscending());
    }

    @PutMapping("/role")
    public ResponseEntity<Object> changeRole(@Valid @RequestBody RoleRequestDto roleDto) {
        if (roleDto.getRole().equalsIgnoreCase("support") ||
                roleDto.getRole().equalsIgnoreCase("merchant")) {
            return userService.setRole(roleDto);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/access")
    public ResponseEntity<StatusResponseDto> locking(@Valid @RequestBody LockingRequestDto lockingDto) {
        return ResponseEntity.ok(userService.locking(lockingDto));
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<UserDeletedResponseDto> deleteUser(@PathVariable String username) {
        boolean isDeleted = userService.deleteUser(username);
        if (!isDeleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new UserDeletedResponseDto(username, "Deleted successfully!"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException() {
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
