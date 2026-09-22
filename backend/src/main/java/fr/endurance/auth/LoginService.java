package fr.endurance.auth;

import fr.endurance.user.User;
import fr.endurance.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    /**
     * Haché de rien du tout, comparé quand l'email est inconnu : la réponse prend alors le même
     * temps qu'un mauvais mot de passe, et le chronomètre ne révèle pas quels emails existent.
     */
    private final String decoyHash;

    public LoginService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.decoyHash = passwordEncoder.encode("leurre-pour-un-email-inconnu");
    }

    @Transactional(readOnly = true)
    public User authenticate(LoginRequest request) {
        User user = users.findByEmail(request.email()).orElse(null);
        String hash = user != null ? user.getPasswordHash() : decoyHash;
        boolean matches = passwordEncoder.matches(request.password(), hash);
        if (user == null || !matches) {
            throw new InvalidCredentialsException();
        }
        return user;
    }
}
