package fr.endurance.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Email invalide")
        @Size(max = 254, message = "254 caractères maximum")
        String email,

        // BCrypt ne lit que les 72 premiers octets : au-delà, la fin serait ignorée en silence.
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 12, message = "12 caractères minimum")
        @Size(max = 72, message = "72 caractères maximum")
        String password,

        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 60, message = "60 caractères maximum")
        String displayName) {
}
