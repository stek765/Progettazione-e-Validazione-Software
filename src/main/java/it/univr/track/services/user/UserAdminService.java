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
    // Implementazione del servizio di amministrazione utenti

    public List<UserRegistered> getAllUsers() {
        // Metodo per ottenere tutti gli utenti
        log.info("Recupero della lista di tutti gli utenti");
        return List.of();
    }


}
