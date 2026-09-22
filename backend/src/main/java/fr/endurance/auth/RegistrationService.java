package fr.endurance.auth;

import fr.endurance.user.User;
import fr.endurance.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegistrationRequest request) {
        if (users.existsByEmail(request.email())) {
            throw new EmailAlreadyUsedException();
        }
        String hash = passwordEncoder.encode(request.password());
        return users.save(new User(request.email(), hash, request.displayName().strip()));
    }
}
