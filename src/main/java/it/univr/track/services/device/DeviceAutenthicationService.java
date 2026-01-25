package it.univr.track.services.device;

import it.univr.track.entity.Device;
import it.univr.track.record.DeviceAuthenticationResultRecord;
import it.univr.track.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static it.univr.track.entity.enumeration.DeviceStatus.ACTIVE;

/**
 * Service adibito all'autenticazione di un device
 */
@Service
@RequiredArgsConstructor
public class DeviceAutenthicationService {

    private final DeviceRepository deviceRepository;

    public DeviceAuthenticationResultRecord autenticazioneDevice(Device device) {

        return (deviceRepository.findById(device.getId()).isPresent() && device.getStatus().equals(ACTIVE))
                ? new DeviceAuthenticationResultRecord(true, null)
                : new DeviceAuthenticationResultRecord(false,
                "Device con id " + device.getId() + " non presente o con credenziali revocate"
        );
    }
}
