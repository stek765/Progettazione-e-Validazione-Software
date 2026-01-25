package it.univr.track.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import it.univr.track.entity.enumeration.Gender;
import it.univr.track.entity.enumeration.Role;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class TrackData extends UserRegistered {


}
