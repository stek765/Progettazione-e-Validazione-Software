package it.univr.track.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ProvisioningToken extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used;


    public ProvisioningToken(String token, Instant expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.used = false;
    }
}

