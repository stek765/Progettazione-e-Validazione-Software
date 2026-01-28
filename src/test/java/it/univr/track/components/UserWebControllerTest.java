package it.univr.track.components;

import it.univr.track.SmartTrackApplication;
import it.univr.track.controller.web.UserWebController;
import it.univr.track.dto.PasswordChangeDTO;
import it.univr.track.dto.UserProfileDTO;
import it.univr.track.dto.UserRegistrationDTO;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.Gender;
import it.univr.track.entity.enumeration.Role;
import it.univr.track.user.UserRepository;
import it.univr.track.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserWebController.class)
@ContextConfiguration(classes = SmartTrackApplication.class)
@ActiveProfiles("gestione-utenti")
@Import(SecurityConfig.class)
public class UserWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private UserRegistered user;

    @BeforeEach
    void setUp() {
        user = UserRegistered.builder()
                .username("testUser")
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .city("Verona")
                .address("Via Roma 1")
                .telephoneNumber("1234567890")
                .taxIdentificationNumber("TAX123")
                .build();
    }

    @Test
    void home_ShouldRedirectToDashboard() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void dashboard_Authenticated_ShouldShowDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attribute("username", "testUser"))
                .andExpect(model().attributeExists("activeDevicesCount", "alertsCount"));
    }

    @Test
    void dashboard_Unauthenticated_ShouldBeProtected() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    // SignUp Tests

    @Test
    void signUpForm_ShouldReturnView() throws Exception {
        mockMvc.perform(get("/signUp"))
                .andExpect(status().isOk())
                .andExpect(view().name("signUp"))
                .andExpect(model().attributeExists("userRegistrationDTO"));
    }

    @Test
    void doSignUp_Success() throws Exception {
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPwd");

        mockMvc.perform(post("/signUp")
                .with(csrf())
                .param("username", "newUser")
                .param("password", "Password123!") // Valid password
                .param("confirmPassword", "Password123!")
                .param("firstname", "New")
                .param("lastname", "User")
                .param("email", "new@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signIn"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userRepository).save(any(UserRegistered.class));
    }

    @Test
    void doSignUp_ValidationErrors() throws Exception {
        mockMvc.perform(post("/signUp")
                .with(csrf())
                .param("username", "") // Invalid
                .param("password", "weak") // Invalid
                .param("confirmPassword", "mismatch"))
                .andExpect(status().isOk())
                .andExpect(view().name("signUp"))
                .andExpect(model().hasErrors()); // Validation fails on @NotBlank and manual checks
    }

    @Test
    void doSignUp_DuplicateUsername() throws Exception {
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/signUp")
                .with(csrf())
                .param("username", "existingUser")
                .param("password", "Password123!")
                .param("confirmPassword", "Password123!")
                .param("firstname", "New")
                .param("lastname", "User")
                .param("email", "new@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("signUp"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "username"));
    }

    @Test
    void doSignUp_PasswordMismatch() throws Exception {
        mockMvc.perform(post("/signUp")
                .with(csrf())
                .param("username", "newUser")
                .param("password", "Password123!")
                .param("confirmPassword", "Password1234!") // Mismatch
                .param("firstname", "New")
                .param("lastname", "User")
                .param("email", "new@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("signUp"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "confirmPassword"));
    }

    // SignIn Tests

    @Test
    void signInForm_ShouldReturnView() throws Exception {
        mockMvc.perform(get("/signIn"))
                .andExpect(status().isOk())
                .andExpect(view().name("signIn"));
    }

    @Test
    void signInForm_WithError() throws Exception {
        mockMvc.perform(get("/signIn").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("signIn"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    // Profile Tests

    @Test
    void showProfile_Unauthenticated_ShouldBeProtected() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser")
    void showProfile_Authenticated_ShouldReturnView() throws Exception {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("userProfile", "passwordChangeDTO"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void updateProfile_Success() throws Exception {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Use strict validation params as required by UserProfileDTO
        mockMvc.perform(post("/profile")
                .with(csrf())
                .param("firstname", "UpdatedName")
                .param("lastname", "UpdatedLast")
                .param("email", "test@example.com")
                .param("city", "Roma")
                .param("address", "Via Milano 2")
                .param("telephoneNumber", "0987654321")
                .param("taxIdentificationNumber", "TAX999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userRepository).save(any(UserRegistered.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    void updateProfile_ValidationErrors() throws Exception {
        // Missing required fields (City, Address, etc.)
        mockMvc.perform(post("/profile")
                .with(csrf())
                .param("firstname", "UpdatedName")
                .param("lastname", "UpdatedLast"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().hasErrors());
    }

    // Change Password Tests

    @Test
    @WithMockUser(username = "testUser")
    void changePassword_Success() throws Exception {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("NewPass1!")).thenReturn("newEncodedPass");

        mockMvc.perform(post("/profile/change-password")
                .with(csrf())
                .param("oldPassword", "oldPass")
                .param("newPassword", "NewPass1!")
                .param("confirmPassword", "NewPass1!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userRepository).save(any(UserRegistered.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    void changePassword_WrongOldPassword() throws Exception {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

        mockMvc.perform(post("/profile/change-password")
                .with(csrf())
                .param("oldPassword", "wrongPass")
                .param("newPassword", "NewPass1!")
                .param("confirmPassword", "NewPass1!"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeHasFieldErrors("passwordChangeDTO", "oldPassword"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void changePassword_Mismatch() throws Exception {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encodedPassword")).thenReturn(true);

        mockMvc.perform(post("/profile/change-password")
                .with(csrf())
                .param("oldPassword", "oldPass")
                .param("newPassword", "NewPass1!")
                .param("confirmPassword", "DifferentPass1!"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeHasFieldErrors("passwordChangeDTO", "confirmPassword"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void changePassword_WeakPassword() throws Exception {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encodedPassword")).thenReturn(true);

        mockMvc.perform(post("/profile/change-password")
                .with(csrf())
                .param("oldPassword", "oldPass")
                .param("newPassword", "weak")
                .param("confirmPassword", "weak"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeHasFieldErrors("passwordChangeDTO", "newPassword"));
    }

    // Users List Tests (Utility)

    @Test
    @WithMockUser(username = "testUser")
    void users_ShouldListAllUsers() throws Exception {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(userRepository.count()).thenReturn(1L);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users", "count"));
    }
}
