package it.univr.track.entity;

import it.univr.track.entity.enumeration.DeviceStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter
@Getter
/**
 * Rappresenta un dispositivo fisico (sensore) registrato nella piattaforma.
 * Può essere associato a un utente o rimanere non assegnato.
 */
public class Device extends AbstractEntity {

    private String name;

    @Setter
    private DeviceStatus status;

    @ManyToOne
    private UserRegistered user;

    @Builder
    public Device(String name, DeviceStatus status, UserRegistered user) {
        this.name = name;
        this.status = status;
        this.user = user;
    }

}
