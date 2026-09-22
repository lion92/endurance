package fr.endurance.auth;

import fr.endurance.user.Emails;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String email, @NotBlank String password) {

    public LoginRequest {
        email = Emails.normalize(email);
    }
}
