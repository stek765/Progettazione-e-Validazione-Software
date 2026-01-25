package it.univr.track.dto;

import it.univr.track.entity.enumeration.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegistrationDTO {

    @NotBlank(message = "Username obbligatorio")
    private String username;

    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Email non valida")
    private String email;

    @NotBlank(message = "Password obbligatoria")
    private String password;

    @NotBlank(message = "Conferma password obbligatoria")
    private String confirmPassword;

    @NotBlank(message = "Nome obbligatorio")
    private String firstname;

    @NotBlank(message = "Cognome obbligatorio")
    private String lastname;

    private Gender gender;

    private String city;

    private String address;

    private String telephoneNumber;

    private String taxIdentificationNumber;
}
