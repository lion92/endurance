package fr.endurance.support;

import static fr.endurance.support.TestData.uniqueEmail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

/** Les gestes d'un utilisateur, réutilisés par tous les tests d'API. */
public final class Accounts {

    public static final String PASSWORD = "un-mot-de-passe-solide";

    private final MockMvcTester mvc;

    public Accounts(MockMvcTester mvc) {
        this.mvc = mvc;
    }

    public String registerNewAthlete() {
        String email = uniqueEmail();
        assertThat(register(email, PASSWORD, "Léa")).hasStatus(HttpStatus.CREATED);
        return email;
    }

    public MvcTestResult register(String email, String password, String displayName) {
        return post("/api/auth/register", """
                {"email": "%s", "password": "%s", "displayName": "%s"}
                """.formatted(email, password, displayName));
    }

    public MvcTestResult login(String email, String password) {
        return post("/api/auth/login", """
                {"email": "%s", "password": "%s"}
                """.formatted(email, password));
    }

    /** Crée un compte, se connecte, et rend le cookie de session à joindre aux requêtes. */
    public Cookie loggedInAthlete() {
        MvcTestResult login = login(registerNewAthlete(), PASSWORD);
        assertThat(login).hasStatus(HttpStatus.OK);
        return login.getResponse().getCookie("access_token");
    }

    private MvcTestResult post(String uri, String json) {
        return mvc.post().uri(uri).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(json)
                .exchange();
    }
}
