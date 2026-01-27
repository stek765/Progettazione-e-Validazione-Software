package it.univr.track.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordChangeDTO {

    @NotBlank(message = "La password attuale è obbligatoria")
    private String oldPassword;

    @NotBlank(message = "La nuova password è obbligatoria")
    private String newPassword;

    @NotBlank(message = "La conferma della password è obbligatoria")
    private String confirmPassword;
}
