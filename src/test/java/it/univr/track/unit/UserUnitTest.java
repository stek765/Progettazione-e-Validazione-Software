package it.univr.track.unit;

import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserUnitTest {

    @Test
    void testUserBuilderAndFields() {
        UserRegistered user = UserRegistered.builder()
                .username("testuser")
                .firstname("Mario")
                .lastname("Rossi")
                .email("mario.rossi@example.com")
                .role(Role.USER)
                .build();

        assertEquals("testuser", user.getUsername());
        assertEquals("Mario", user.getFirstname());
        assertEquals("Rossi", user.getLastname());
        assertEquals("mario.rossi@example.com", user.getEmail());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void testUserDefaults() {
        // Verifica che un utente creato abbia certi default se non specificati
        // Nota: Il builder potrebbe non settare tutto di default se non impostato nella
        // classe.
        // UserRegistered usa @NoArgsConstructor quindi verifichiamo il costruttore
        // vuoto.
        UserRegistered user = new UserRegistered();
        assertNull(user.getUsername());
        assertNull(user.getRole());

        // Verifica logica custom se presente (es. failedLoginAttempts a 0)
        assertEquals(0, user.getFailedLoginAttempts());
    }
}
