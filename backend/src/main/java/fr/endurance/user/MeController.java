package fr.endurance.user;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class MeController {

    private final CurrentUser currentUser;

    public MeController(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return UserResponse.from(currentUser.of(jwt));
    }

    @PutMapping
    @Transactional
    public UserResponse update(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProfileUpdate update) {
        User user = currentUser.of(jwt);
        user.updateProfile(update);
        return UserResponse.from(user);
    }
}
