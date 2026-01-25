package it.univr.track.services.user;

import it.univr.track.entity.UserRegistered;
import it.univr.track.exceptions.UserDataException;
import it.univr.track.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;

    public void registrazioneNuovoUtente(UserRegistered userRegistered) {

        String normalized = normalize(userRegistered.getEmail());

        if (userRepository.existsByEmailIgnoreCase(normalized)) {
            throw new UserDataException("Email già registrata: " + normalized);
        }

        userRepository.save(userRegistered);
    }

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

}
