package it.univr.track.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Shipment extends AbstractEntity {

    @OneToMany
    private List<Device> devices;


}
