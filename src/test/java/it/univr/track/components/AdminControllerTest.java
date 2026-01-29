package it.univr.track.components;

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
/**
 * Component Test per il controller AdminWebController.
 * Verifica la logica di assegnazione dispositivi isolando il controller tramite
 * MockMvc.
 * Simula il comportamento del livello dati (Repository) senza accedere al DB
 * reale.
 */
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DeviceRepository deviceRepository;

    // Verifica che la pagina di gestione sia accessibile all'admin e che il modello
    // contenga i dati attesi
    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void testAdminPageAccess() throws Exception {
        mockMvc.perform(get("/web/utenti-e-dispositivi"))
                .andExpect(status().isOk())
                .andExpect(view().name("deviceAndUsers"))
                .andExpect(model().attributeExists("usersList"))
                .andExpect(model().attributeExists("unassignedDevices"));
    }

    // Verifica che l'accesso sia negato agli utenti non autenticati
    @Test
    void testAccessDeniedForAnonymous() throws Exception {
        mockMvc.perform(get("/web/utenti-e-dispositivi"))
                .andExpect(status().isUnauthorized());
    }

    // Testa l'assegnazione corretta di un dispositivo a un utente
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
                .param("deviceId", "1")
                .param("targetId", "user-testuser"))
                .andExpect(status().isOk());

        verify(deviceRepository).save(mockDevice);
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
                .param("deviceId", "2")
                .param("targetId", "unassigned-pool"))
                .andExpect(status().isOk());

        verify(deviceRepository).save(mockDevice);
        assert (mockDevice.getUser() == null);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void testAssignDeviceNotFound() throws Exception {
        when(deviceRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/web/api/assign-device")
                .with(csrf())
                .param("deviceId", "999")
                .param("targetId", "unassigned-pool"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Device not found"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void testAssignDeviceUserNotFound() throws Exception {
        Device mockDevice = new Device();
        ReflectionTestUtils.setField(mockDevice, "id", 3L);
        when(deviceRepository.findById(3L)).thenReturn(Optional.of(mockDevice));
        when(userRepository.findByUsername("ghost-user")).thenReturn(Optional.empty());

        mockMvc.perform(post("/web/api/assign-device")
                .with(csrf())
                .param("deviceId", "3")
                .param("targetId", "user-ghost-user"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void testToggleProvisionNotFound() throws Exception {
        when(deviceRepository.findById(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/web/device-mock/999/provision")
                .with(csrf())
                .param("provisioned", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }
}
