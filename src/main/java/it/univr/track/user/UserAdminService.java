package it.univr.track.user;

import it.univr.track.entity.UserRegistered;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

// This service provides administrative operations for managing users.
// they are here and not in the controller because they might be used also by other services.
@Service
@RequiredArgsConstructor
@Log4j2
public class UserAdminService {

    private final UserRepository userRepository;

    public List<UserRegistered> getAllUsers() {
        log.info("Recupero della lista di tutti gli utenti");
        return (List<UserRegistered>) userRepository.findAll();
    }

    public UserRegistered createUser(UserRegistered user) {
        log.info("Creating new user: {}", user.getEmail());
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        return userRepository.save(user);
    }
}
