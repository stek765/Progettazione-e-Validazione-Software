package it.univr.track.user;

import it.univr.track.entity.UserRegistered;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService service;

    @Test
    void createUser_Success() {
        UserRegistered user = new UserRegistered();
        user.setEmail("new@test.com");

        when(userRepository.existsByEmailIgnoreCase("new@test.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        UserRegistered result = service.createUser(user);
        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void createUser_DuplicateEmail() {
        UserRegistered user = new UserRegistered();
        user.setEmail("exists@test.com");

        when(userRepository.existsByEmailIgnoreCase("exists@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new UserRegistered(), new UserRegistered()));
        List<UserRegistered> users = service.getAllUsers();
        assertEquals(2, users.size());
    }
}
