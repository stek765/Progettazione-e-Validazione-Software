package it.univr.track.services.user;

import it.univr.track.entity.UserRegistered;
import it.univr.track.exceptions.AuthenticationFailedException;
import it.univr.track.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserAuthenticationService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_SECONDS = 60; // 1 minuto (per test); in prod magari 300+
    private static final String GENERIC_ERROR = "Credenziali non valide";
    private final UserRepository userRepository;

    @Transactional
    public void login(String email, String rawPassword) {

        // email normalizzata
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();

        UserRegistered user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> {
                    // Non rivelare se esiste o no
                    log.warn("SECURITY: login failed for non-existing email={}", normalizedEmail);
                    return new AuthenticationFailedException(GENERIC_ERROR);
                });

        Instant now = Instant.now();

        // se lock attivo: fallisci anche se password corretta
        if (user.getLockedUntil() != null && now.isBefore(user.getLockedUntil())) {
            log.warn("SECURITY: account locked email={}", user.getEmail());
            throw new AuthenticationFailedException(GENERIC_ERROR);
        }

        // verifica password (qui plain per semplicità; in prod usa PasswordEncoder)
        boolean passwordOk = user.getPassword() != null && user.getPassword().equals(rawPassword);

        if (!passwordOk) {
            int next = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(next);

            if (next >= MAX_ATTEMPTS) {
                user.setLockedUntil(now.plusSeconds(LOCK_SECONDS));
                log.warn("SECURITY: account locked due to failed attempts email={}", user.getEmail());
            } else {
                log.warn("SECURITY: login failed email={} attempts={}", user.getEmail(), next);
            }

            userRepository.save(user);
            throw new AuthenticationFailedException(GENERIC_ERROR);
        }

        // successo: reset contatori
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }
}
