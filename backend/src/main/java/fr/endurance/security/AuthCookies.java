package fr.endurance.security;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * Le jeton voyage dans un cookie :
 * - HttpOnly : invisible pour JavaScript, donc hors de portée d'une faille XSS ;
 * - SameSite=Strict : jamais envoyé par une requête partie d'un autre site ;
 * - Path=/api : seules les requêtes vers l'API le transportent.
 */
@Component
public class AuthCookies {

    public static final String NAME = "access_token";

    private final JwtProperties properties;

    public AuthCookies(JwtProperties properties) {
        this.properties = properties;
    }

    public ResponseCookie session(String token) {
        return base(token).maxAge(properties.ttl()).build();
    }

    public ResponseCookie cleared() {
        return base("").maxAge(Duration.ZERO).build();
    }

    private ResponseCookie.ResponseCookieBuilder base(String value) {
        return ResponseCookie.from(NAME, value)
                .httpOnly(true)
                .secure(properties.secureCookie())
                .sameSite("Strict")
                .path("/api");
    }
}
