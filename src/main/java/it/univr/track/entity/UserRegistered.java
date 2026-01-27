package it.univr.track.entity;

import it.univr.track.entity.enumeration.Gender;
import it.univr.track.entity.enumeration.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Locale;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class UserRegistered extends AbstractEntity {

    @Column(unique = true)
    protected String username;
    protected String password;
    @NotBlank(message = "Nome obbligatorio")
    protected String firstname;
    @NotBlank(message = "Cognome obbligatorio")
    protected String lastname;
    @Setter(AccessLevel.NONE)
    protected String email;
    protected String address;
    protected String telephoneNumber;
    protected String city;
    protected String taxIdentificationNumber;
    @Enumerated(EnumType.STRING)
    protected Role role;
    @Enumerated(EnumType.STRING)
    protected Gender gender;
    private int failedLoginAttempts;
    private Instant lockedUntil;

    @Builder
    public UserRegistered(String firstname, String lastname, String username, String password, String email, Role role,
            Gender gender, String city, String address, String telephoneNumber, String taxIdentificationNumber) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.gender = gender;
        this.city = city;
        this.address = address;
        this.telephoneNumber = telephoneNumber;
        this.taxIdentificationNumber = taxIdentificationNumber;
        this.failedLoginAttempts = 0;
    }

    /**
     * Normalizza l'email in lower-case
     */
    @PrePersist
    @PreUpdate
    private void normalizEmail() {
        if (this.email != null) {
            this.email = this.email.trim().toLowerCase(Locale.ROOT);
        }
    }

    public void setEmail(String mail) {
        this.email = mail.trim().toLowerCase(Locale.ROOT);
    }
}
