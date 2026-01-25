package it.univr.track.services.device;

import it.univr.track.entity.Device;
import it.univr.track.entity.ProvisioningToken;
import it.univr.track.exceptions.ProvisioningNotAllowedException;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.repository.ProvisioningTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DeviceProvisioningService {

    private final ProvisioningTokenRepository tokenRepository;
    private final DeviceRepository deviceRepository;
    private final Clock clock; // in test lo controlli

    @Transactional
    public void provisionDevice(String tokenValue, Device deviceToAssociate) {
        ProvisioningToken pt = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new ProvisioningNotAllowedException("Token provisioning non valido"));

        Instant now = Instant.now(clock);

        if (pt.isUsed()) {
            throw new ProvisioningNotAllowedException("Token provisioning già usato");
        }

        if (now.isAfter(pt.getExpiresAt())) {
            // invalidazione: lo segno usato così non è riutilizzabile
            pt.setUsed(true);
            tokenRepository.save(pt);
            throw new ProvisioningNotAllowedException("Token provisioning scaduto");
        }

        // token valido -> lo consumo (one-time)
        pt.setUsed(true);
        tokenRepository.save(pt);

        // associare device a user/tenant: adatta ai tuoi campi reali
        // deviceToAssociate.setUserId(pt.getUserId());
        // deviceToAssociate.setTenantId(pt.getTenantId());

        deviceRepository.save(deviceToAssociate);
    }
}
