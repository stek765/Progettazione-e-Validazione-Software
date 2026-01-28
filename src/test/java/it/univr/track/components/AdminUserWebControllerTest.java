package it.univr.track.components;

import it.univr.track.controller.web.AdminUserWebController;
import it.univr.track.dto.UserEditDTO;
import it.univr.track.dto.UserRegistrationDTO;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.Gender;
import it.univr.track.entity.enumeration.Role;
import it.univr.track.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUserWebController.class)
class AdminUserWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void listUsers_ShouldReturnViewAndUsers() throws Exception {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("usersManagmentAdmin"))
                .andExpect(model().attributeExists("users"));

        verify(userRepository).findAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void deleteUser_WhenDeletingSelf_ShouldFail() throws Exception {
        mockMvc.perform(post("/admin/users/delete")
                .param("usernameToDelete", "admin")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("errorMessage", "Non puoi eliminare il tuo stesso account."));

        verify(userRepository, never()).delete(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void deleteUser_WhenDeletingOther_ShouldSuccess() throws Exception {
        UserRegistered otherUser = new UserRegistered();
        otherUser.setUsername("other");
        when(userRepository.findByUsername("other")).thenReturn(Optional.of(otherUser));

        mockMvc.perform(post("/admin/users/delete")
                .param("usernameToDelete", "other")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("successMessage", "Utente other eliminato con successo."));

        verify(userRepository).delete(otherUser);
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void registerUserForm_ShouldReturnView() throws Exception {
        mockMvc.perform(get("/admin/users/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("signUp"))
                .andExpect(model().attributeExists("userRegistrationDTO"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void registerUser_Success() throws Exception {
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        mockMvc.perform(post("/admin/users/register")
                .param("username", "newUser")
                .param("password", "password")
                .param("confirmPassword", "password")
                .param("firstname", "Mario")
                .param("lastname", "Rossi")
                .param("email", "mario@example.com")
                .param("role", "USER")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("successMessage", "Nuovo utente registrato con successo."));

        verify(userRepository).save(any(UserRegistered.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void registerUser_AlreadyExists() throws Exception {
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new UserRegistered()));

        mockMvc.perform(post("/admin/users/register")
                .param("username", "existingUser")
                .param("password", "password")
                .param("confirmPassword", "password")
                .param("firstname", "Mario")
                .param("lastname", "Rossi")
                .param("email", "mario@example.com")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("signUp"))
                .andExpect(model().attribute("errorMessage", "Username già esistente."));

        verify(userRepository, never()).save(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void editUserForm_UserFound() throws Exception {
        UserRegistered user = new UserRegistered();
        user.setUsername("targetUser");
        user.setFirstname("Mario");
        user.setLastname("Rossi");
        user.setEmail("test@test.com");
        user.setCity("Verona");
        user.setAddress("Via Roma 1");
        user.setTelephoneNumber("123456789");
        user.setTaxIdentificationNumber("ABCDEF12G34H567I");

        when(userRepository.findByUsername("targetUser")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/admin/users/edit/targetUser"))
                .andExpect(status().isOk())
                .andExpect(view().name("editUserAdmin"))
                .andExpect(model().attributeExists("userEditDTO"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void editUserForm_UserNotFound() throws Exception {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/users/edit/unknownUser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("errorMessage", "Utente non trovato."));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void updateUser_Success() throws Exception {
        UserRegistered existingUser = new UserRegistered();
        existingUser.setUsername("targetUser");
        when(userRepository.findByUsername("targetUser")).thenReturn(Optional.of(existingUser));

        mockMvc.perform(post("/admin/users/update")
                .param("username", "targetUser")
                .param("firstname", "Luigi")
                .param("lastname", "Verdi")
                .param("email", "luigi@example.com")
                .param("city", "Milano")
                .param("address", "Via Dante 10")
                .param("telephoneNumber", "987654321")
                .param("taxIdentificationNumber", "LGUVRD80A01H501Z")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("successMessage", "Utente aggiornato con successo."));

        verify(userRepository).save(existingUser);
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void updateUser_WithPasswordChange() throws Exception {
        UserRegistered existingUser = new UserRegistered();
        existingUser.setUsername("targetUser");
        when(userRepository.findByUsername("targetUser")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        mockMvc.perform(post("/admin/users/update")
                .param("username", "targetUser")
                .param("firstname", "Luigi")
                .param("lastname", "Verdi")
                .param("email", "luigi@example.com")
                .param("password", "newPass")
                .param("city", "Milano")
                .param("address", "Via Dante 10")
                .param("telephoneNumber", "987654321")
                .param("taxIdentificationNumber", "LGUVRD80A01H501Z")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userRepository).save(any(UserRegistered.class));
        verify(passwordEncoder).encode("newPass");
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void updateUser_UserNotFound() throws Exception {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        mockMvc.perform(post("/admin/users/update")
                .param("username", "unknownUser")
                .param("firstname", "Ghost")
                .param("lastname", "User")
                .param("email", "ghost@example.com")
                .param("city", "Nowhere")
                .param("address", "Void 0")
                .param("telephoneNumber", "000000000")
                .param("taxIdentificationNumber", "GHOST00A01H501X")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("errorMessage", "Errore durante l'aggiornamento."));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void updateUser_ValidationErrors() throws Exception {
        mockMvc.perform(post("/admin/users/update")
                .param("username", "") // Invalid empty username
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("editUserAdmin"))
                .andExpect(model().attributeHasErrors("userEditDTO"));
    }
}
