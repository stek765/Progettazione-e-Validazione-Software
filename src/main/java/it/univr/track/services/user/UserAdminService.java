package it.univr.track.services.user;

import it.univr.track.entity.UserRegistered;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserAdminService {

    private final it.univr.track.repository.UserRepository userRepository;

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
