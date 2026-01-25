package it.univr.track.services.device;

import it.univr.track.entity.Device;
import it.univr.track.entity.ProvisioningToken;
import it.univr.track.exceptions.ProvisioningNotAllowedException;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.repository.ProvisioningTokenRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceProvisioningServiceTest {

    @Mock
    private ProvisioningTokenRepository tokenRepository;
    @Mock
    private DeviceRepository deviceRepository;

    private DeviceProvisioningService service;
    private final Instant NOW = Instant.parse("2023-01-01T10:00:00Z");

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(NOW, ZoneId.of("UTC"));
        service = new DeviceProvisioningService(tokenRepository, deviceRepository, fixedClock);
    }

    @Test
    void provision_Success() {
        String tokenVal = "ABC";
        ProvisioningToken token = new ProvisioningToken();
        token.setToken(tokenVal);
        token.setExpiresAt(NOW.plusSeconds(60));
        token.setUsed(false);

        Device device = new Device();

        when(tokenRepository.findByToken(tokenVal)).thenReturn(Optional.of(token));

        service.provisionDevice(tokenVal, device);

        assertTrue(token.isUsed());
        verify(tokenRepository).save(token);
        verify(deviceRepository).save(device);
    }

    @Test
    void provision_Expired() {
        String tokenVal = "EXP";
        ProvisioningToken token = new ProvisioningToken();
        token.setExpiresAt(NOW.minusSeconds(1)); // Expired
        token.setUsed(false);

        when(tokenRepository.findByToken(tokenVal)).thenReturn(Optional.of(token));

        assertThrows(ProvisioningNotAllowedException.class, () -> service.provisionDevice(tokenVal, new Device()));
        assertTrue(token.isUsed()); // Should be marked used to invalidate
        verify(tokenRepository).save(token);
    }
}
