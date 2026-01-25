package it.univr.track;
/*
 * 3. Gestione Dispositivi
 * 3.a Assegnazione Dispositivi
 * 3.b Revoca Dispositivi
 * 3.c Monitoraggio Dispositivi
*/

import it.univr.track.entity.Device;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.repository.ProvisioningTokenRepository;
import it.univr.track.repository.UserRepository;
import it.univr.track.services.device.DeviceAutenthicationService;
import it.univr.track.services.device.DeviceProvisioningService;
import it.univr.track.services.user.UserAuthenticationService;
import it.univr.track.services.user.UserRegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class DeviceManagerTest {
    //TODO: implement test cases for device management
    @Autowired
    UserRegistrationService userRegistrationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private DeviceAutenthicationService deviceAutenthicationService;
    @Autowired
    private ProvisioningTokenRepository tokenRepository;
    @Autowired
    private DeviceProvisioningService provisioningService;
    @Autowired
    private UserAuthenticationService authService;
    @Autowired
    private MockMvc mockMvc;


    @Test
    public void visualizzaListaDispositivi() {
        //TODO: implement test case for viewing device list
    }
}
