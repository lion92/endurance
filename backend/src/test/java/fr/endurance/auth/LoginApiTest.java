package fr.endurance.auth;

import static fr.endurance.support.Accounts.PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import fr.endurance.security.JwtProperties;
import fr.endurance.security.TokenService;
import fr.endurance.support.Accounts;
import fr.endurance.support.IntegrationTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import fr.endurance.user.User;
import fr.endurance.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@IntegrationTest
class LoginApiTest {

    @Autowired
    MockMvcTester mvc;

    @Autowired
    UserRepository users;

    @Autowired
    JwtEncoder encoder;

    @Autowired
    JwtProperties properties;

    Accounts accounts;

    @BeforeEach
    void setUp() {
        accounts = new Accounts(mvc);
    }

    @Test
    void la_connexion_pose_un_cookie_que_javascript_ne_peut_pas_lire() {
        String email = accounts.registerNewAthlete();

        MvcTestResult login = accounts.login(email, PASSWORD);

        assertThat(login).hasStatus(HttpStatus.OK);
        assertThat(login.getResponse().getHeader("Set-Cookie"))
                .startsWith("access_token=")
                .contains("HttpOnly", "SameSite=Strict", "Path=/api");
    }

    @Test
    void un_mauvais_mot_de_passe_est_refuse() {
        String email = accounts.registerNewAthlete();

        assertThat(accounts.login(email, "pas-le-bon-mot-de-passe"))
                .hasStatus(HttpStatus.UNAUTHORIZED)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("Email ou mot de passe incorrect");
    }

    @Test
    void un_email_inconnu_recoit_exactement_la_meme_reponse() {
        assertThat(accounts.login("personne@endurance.test", PASSWORD))
                .hasStatus(HttpStatus.UNAUTHORIZED)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("Email ou mot de passe incorrect");
    }

    @Test
    void sans_cookie_on_ne_sait_pas_qui_vous_etes() {
        assertThat(mvc.get().uri("/api/me")).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void avec_le_cookie_l_api_vous_reconnait() {
        String email = accounts.registerNewAthlete();
        Cookie session = accounts.login(email, PASSWORD).getResponse().getCookie("access_token");

        assertThat(mvc.get().uri("/api/me").cookie(session))
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .extractingPath("$.email").isEqualTo(email);
    }

    @Test
    void un_jeton_fabrique_a_la_main_est_rejete() {
        Cookie forged = new Cookie("access_token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIn0.faux");

        assertThat(mvc.get().uri("/api/me").cookie(forged)).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void un_jeton_expire_est_rejete() {
        String email = accounts.registerNewAthlete();
        User athlete = users.findByEmail(email).orElseThrow();
        Clock threeHoursAgo = Clock.fixed(Instant.now().minus(Duration.ofHours(3)), ZoneOffset.UTC);
        String expired = new TokenService(encoder, properties, threeHoursAgo).issueFor(athlete);

        assertThat(mvc.get().uri("/api/me").cookie(new Cookie("access_token", expired)))
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void la_deconnexion_efface_le_cookie() {
        MvcTestResult logout = mvc.post().uri("/api/auth/logout").with(csrf()).exchange();

        assertThat(logout).hasStatus(HttpStatus.NO_CONTENT);
        assertThat(logout.getResponse().getHeader("Set-Cookie"))
                .startsWith("access_token=;")
                .contains("Max-Age=0");
    }
}
