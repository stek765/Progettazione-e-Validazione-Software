package it.univr.track.controller;

import it.univr.track.controller.web.AdminWebController;
import it.univr.track.device.DeviceRepository;
import it.univr.track.entity.Device;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.Role;
import it.univr.track.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AdminWebController.class)
// Importante: a volte serve importare la SecurityConfig se definita a parte,
// ma con @WebMvcTest e @WithMockUser spesso basta questo per testare il
// controller isolato.
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DeviceRepository deviceRepository;

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void testAdminPageAccess() throws Exception {
        mockMvc.perform(get("/web/utenti-e-dispositivi"))
                .andExpect(status().isOk())
                .andExpect(view().name("deviceAndUsers"))
                .andExpect(model().attributeExists("usersList"))
                .andExpect(model().attributeExists("unassignedDevices"));
    }

    @Test
    // Senza utente -> dovrebbe essere redirect al login o 401/403 a seconda delle
    // config
    // Dato che stiamo testando solo il controller (slice test), Spring Security di
    // default
    // su @WebMvcTest protegge gli endpoint. Se non siamo autenticati -> 401
    // Unauthorized nei test default api
    // o 302 Login Page. Verifichiamo il 401/403 o 302.
    // In questo progetto sembra che usiamo i form login.
    void testAccessDeniedForAnonymous() throws Exception {
        mockMvc.perform(get("/web/utenti-e-dispositivi"))
                .andExpect(status().isUnauthorized()); // Spesso nei test MVC slice senza full config, torna 401
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void testAssignDeviceSuccess() throws Exception {
        Device mockDevice = new Device();
        ReflectionTestUtils.setField(mockDevice, "id", 1L);
        mockDevice.setName("Test Device");

        UserRegistered mockUser = new UserRegistered();
        mockUser.setUsername("testuser");

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(mockDevice));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/web/api/assign-device")
                .with(csrf())
                .param("targetId", "user-testuser"))
                .andExpect(status().isOk());

        // Verify repository interaction
        verify(deviceRepository).save(mockDevice);
        // Verify state change
        assert (mockDevice.getUser() == mockUser);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void testAssignDeviceUnassign() throws Exception {
        Device mockDevice = new Device();
        ReflectionTestUtils.setField(mockDevice, "id", 2L);
        UserRegistered oldUser = new UserRegistered();
        mockDevice.setUser(oldUser);

        when(deviceRepository.findById(2L)).thenReturn(Optional.of(mockDevice));

        mockMvc.perform(post("/web/api/assign-device")
                .with(csrf())
                .param("targetId", "unassigned-pool"))
                .andExpect(status().isOk());

        verify(deviceRepository).save(mockDevice);
        assert (mockDevice.getUser() == null);
    }
}
