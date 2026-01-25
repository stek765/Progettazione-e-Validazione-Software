package it.univr.track.services.user;

import it.univr.track.entity.UserRegistered;
import it.univr.track.exceptions.AuthenticationFailedException;
import it.univr.track.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserAuthenticationService service;
    private final Instant NOW = Instant.parse("2023-01-01T10:00:00Z");

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(NOW, ZoneId.of("UTC"));
        service = new UserAuthenticationService(userRepository, fixedClock);
    }

    @Test
    void login_Success() {
        UserRegistered user = new UserRegistered();
        user.setEmail("test@test.com");
        user.setPassword("password");

        when(userRepository.findByEmailIgnoreCase("test@test.com")).thenReturn(Optional.of(user));

        service.login("test@test.com", "password");

        assertEquals(0, user.getFailedLoginAttempts());
        assertNull(user.getLockedUntil());
        verify(userRepository).save(user);
    }

    @Test
    void login_WrongPassword() {
        UserRegistered user = new UserRegistered();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setFailedLoginAttempts(0);

        when(userRepository.findByEmailIgnoreCase("test@test.com")).thenReturn(Optional.of(user));

        assertThrows(AuthenticationFailedException.class, () -> service.login("test@test.com", "wrong"));

        assertEquals(1, user.getFailedLoginAttempts());
        verify(userRepository).save(user);
    }

    @Test
    void login_AccountLocking() {
        UserRegistered user = new UserRegistered();
        user.setEmail("test@test.com");
        user.setPassword("password");
        // Assumption: MAX_ATTEMPTS is 3 (from reading the service code)
        user.setFailedLoginAttempts(2);

        when(userRepository.findByEmailIgnoreCase("test@test.com")).thenReturn(Optional.of(user));

        assertThrows(AuthenticationFailedException.class, () -> service.login("test@test.com", "wrong"));

        // Now it should be 3 and locked
        assertEquals(3, user.getFailedLoginAttempts());
        assertNotNull(user.getLockedUntil());
        verify(userRepository).save(user);
    }

    @Test
    void login_LockedAccountRejection() {
        UserRegistered user = new UserRegistered();
        user.setLockedUntil(NOW.plusSeconds(100)); // Locked in future timestamp

        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(user));

        assertThrows(AuthenticationFailedException.class, () -> service.login("test@test.com", "password"));

        verify(userRepository, never()).save(user);
    }
}
