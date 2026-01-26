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
public class Device extends AbstractEntity {

    private String name;

    @Setter
    private DeviceStatus status;

    @Builder
    public Device(String name, DeviceStatus status) {
        this.name = name;
        this.status = status;
    }

}
