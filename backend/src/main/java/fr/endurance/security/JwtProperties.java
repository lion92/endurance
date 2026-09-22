package fr.endurance.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param secret clé HMAC encodée en Base64, 256 bits au moins
 * @param ttl          durée de vie d'un jeton
 * @param secureCookie cookie réservé à HTTPS ; faux seulement en développement local
 */
@ConfigurationProperties("endurance.jwt")
public record JwtProperties(String secret, Duration ttl, boolean secureCookie) {
}
