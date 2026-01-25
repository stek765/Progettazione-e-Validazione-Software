package it.univr.track.repository;

import it.univr.track.entity.ProvisioningToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProvisioningTokenRepository extends CrudRepository<ProvisioningToken, Long> {
    Optional<ProvisioningToken> findByToken(String token);
}
