package fr.endurance.auth;

import static fr.endurance.support.TestData.uniqueEmail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import fr.endurance.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@IntegrationTest
class RegistrationApiTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void un_visiteur_cree_son_compte() {
        String email = uniqueEmail();

        assertThat(register(email, "un-mot-de-passe-solide", "Léa"))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.email").isEqualTo(email);
    }

    @Test
    void la_reponse_ne_contient_jamais_le_mot_de_passe() {
        assertThat(register(uniqueEmail(), "un-mot-de-passe-solide", "Léa"))
                .hasStatus(HttpStatus.CREATED)
                .bodyText()
                .doesNotContain("un-mot-de-passe-solide")
                .doesNotContain("password");
    }

    @Test
    void un_email_deja_utilise_est_refuse() {
        String email = uniqueEmail();
        register(email, "un-mot-de-passe-solide", "Léa");

        assertThat(register(email, "un-autre-mot-de-passe", "Tom"))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.detail").isEqualTo("Cet email est déjà utilisé");
    }

    @Test
    void un_mot_de_passe_trop_court_est_refuse() {
        assertThat(register(uniqueEmail(), "court", "Léa"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.errors.password").isEqualTo("12 caractères minimum");
    }

    private MvcTestResult register(String email, String password, String displayName) {
        return mvc.post().uri("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "%s", "password": "%s", "displayName": "%s"}
                        """.formatted(email, password, displayName))
                .exchange();
    }
}
