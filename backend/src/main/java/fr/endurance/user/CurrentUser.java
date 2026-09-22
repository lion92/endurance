package fr.endurance.user;

import org.springframework.security.oauth2.jwt.Jwt;

/** Le sujet du jeton vérifié par Spring Security, c'est l'identifiant de l'utilisateur. */
public final class CurrentUser {

    private CurrentUser() {
    }

    public static Long idOf(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
