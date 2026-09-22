package fr.endurance.user;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/** Traduit le jeton vérifié par Spring Security en utilisateur de notre domaine. */
@Component
public class CurrentUser {

    private final UserRepository users;

    public CurrentUser(UserRepository users) {
        this.users = users;
    }

    public static Long idOf(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    public User of(Jwt jwt) {
        return users.findById(idOf(jwt)).orElseThrow(UnknownUserException::new);
    }
}
