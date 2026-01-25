package it.univr.track;

import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.Gender;
import it.univr.track.entity.enumeration.Role;
import it.univr.track.repository.UserRepository;
import it.univr.track.services.user.UserAdminService;
import it.univr.track.services.user.UserRegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Scenari da analizzare:
 * 1. Accesso Admin e gestione admin
 * 1.a cambio password admin
 * 2. Gestione Utenti
 * 2.a Visualizzazione Lista Utenti FATTO
 * 2.b Modifica dati utenti
 * 2.c Eliminazione Utenti FATTO
 * 3. Gestione Dispositivi
 * 3.a Assegnazione Dispositivi
 * 3.b Revoca Dispositivi
 * 3.c Monitoraggio Dispositivi
 * ADMIN CREA UN NUOVO UTENTE
 * utente prova a fare accesso ma non ci riesce
 * admin fa accesso
 * admin crea un nuovo utente
 * admin si disconnette
 * utente fa accesso e ci riesce
 *
 * ADMIN CAMBIA I DATI DELL'UTENTE
 * utente si registra
 * fa accesso e vede i suoi dati
 * utente si disconnette
 * admin fa accesso
 * cambia dati utente
 * admin si disconnette
 * utente fa accesso e vede dati cambiati
 *
 * ADMIN ELIMINA UTENTE
 * utente si registra
 * fa accesso e vede la sua schemata
 * utente si disconnette
 * admin fa accesso
 * admin elimina utente
 * admin si disconnette
 * utente prova a fare accesso ma non riesce
 *
 * ADMIN ASSEGNA DISPOSITIVI
 * utente si registra
 * admin fa accesso
 * admin assegna all'utente un dispositivo
 * admin si disconnette
 * utente fa accesso
 * utente vede un nuovo dispositivo
 *
 * ADMIN REVOCA DISPOSITIVO AD UTENTE
 * utente si registra
 * admin fa accesso
 * admin elimina un dispositivo dall'utente
 * admin si disconnette
 * utente fa accesso
 * utente non vede un suo dispositivoù
 */

@SpringBootTest
@AutoConfigureMockMvc
public class UserManagerTest extends BaseTest {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAdminService userAdminService;

    @Test
    public void visualizzaListaUtenti() {
        List<UserRegistered> utenti = userAdminService.getAllUsers();
        assertFalse(utenti.isEmpty(), "la lista utenti NON deve essere vuota (popolata da data.sql)");
    }

    @Test
    public void modificaRuoloUtente() {
        UserRegistered user = new UserRegistered("Noemi", "Morosini", "nmoro", "secretpass", "noemi@gmail.it",
                Role.USER, Gender.FEMALE, "Verona", "Via Roma 1", "1234567890", "MRONMI90A41L378X");
        userRepository.save(user);

        user.setRole(Role.ADMIN);
        userRepository.save(user);

        assertTrue(userRepository.findByUsername("nmoro").isPresent(),
                "L'utente dovrebbe essere presente nel repository dopo il salvataggio");
        assertEquals(Role.ADMIN, userRepository.findByUsername("nmoro").get().getRole());
    }

    @Test
    public void eliminaUtente() {
        UserRegistered user = new UserRegistered("Noemi", "Morosini", "nmoro", "secretpass", "noemi@gmail.it",
                Role.USER, Gender.FEMALE, "Verona", "Via Roma 1", "1234567890", "MRONMI90A41L378X");
        userRepository.save(user);
        assertTrue(userRepository.findByUsername("nmoro").isPresent(),
                "L'utente dovrebbe essere presente nel repository dopo il salvataggio");
        userRepository.delete(user);
        assertFalse(userRepository.findByUsername("nmoro").isPresent(),
                "L'utente non dovrebbe essere più presente nel repository dopo l'eliminazione");

    }

}