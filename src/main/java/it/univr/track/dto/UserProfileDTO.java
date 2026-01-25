package it.univr.track.dto;

import it.univr.track.entity.enumeration.Gender;
import it.univr.track.entity.enumeration.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileDTO {

    private String username;

    @NotBlank(message = "Nome obbligatorio")
    private String firstname;

    @NotBlank(message = "Cognome obbligatorio")
    private String lastname;

    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Email non valida")
    private String email;

    private Gender gender;

    @NotBlank(message = "Città obbligatoria")
    private String city;

    @NotBlank(message = "Indirizzo obbligatorio")
    private String address;

    @NotBlank(message = "Telefono obbligatorio")
    private String telephoneNumber;

    @NotBlank(message = "Codice Fiscale obbligatorio")
    private String taxIdentificationNumber;

    private Role role; // Read only usually, but good to have for display
}
