package it.univr.track.dto;

import it.univr.track.entity.enumeration.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEditDTO {
    // Used to identify the user
    private String username;

    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Email non valida")
    private String email;

    // Optional for update: if null or empty, password remains unchanged
    private String password;

    // Not strictly needed if we don't force reset, but good for UI consistency if they do type a new one
    private String confirmPassword;

    @NotBlank(message = "Nome obbligatorio")
    private String firstname;

    @NotBlank(message = "Cognome obbligatorio")
    private String lastname;

    private Gender gender;

    @NotBlank(message = "Città obbligatoria")
    private String city;

    @NotBlank(message = "Indirizzo obbligatorio")
    private String address;

    @NotBlank(message = "Telefono obbligatorio")
    private String telephoneNumber;

    @NotBlank(message = "Codice Fiscale obbligatorio")
    private String taxIdentificationNumber;
}
