package fr.endurance.auth;

import fr.endurance.security.AuthCookies;
import fr.endurance.security.TokenService;
import fr.endurance.user.User;
import fr.endurance.user.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrationService registration;
    private final LoginService login;
    private final TokenService tokens;
    private final AuthCookies cookies;

    public AuthController(RegistrationService registration, LoginService login,
                          TokenService tokens, AuthCookies cookies) {
        this.registration = registration;
        this.login = login;
        this.tokens = tokens;
        this.cookies = cookies;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegistrationRequest request) {
        return UserResponse.from(registration.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = login.authenticate(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.session(tokens.issueFor(user)).toString())
                .body(UserResponse.from(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookies.cleared().toString())
                .build();
    }
}
