package fr.endurance.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import fr.endurance.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

/**
 * MockMvc n'exécute pas le vrai conteneur de servlets : une erreur n'y est jamais RÉ-EXPÉDIÉE
 * vers /error. Ce test-ci démarre un vrai Tomcat, pour voir ce que voit un vrai navigateur.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class RealServerErrorsTest {

    @LocalServerPort
    int port;

    @Test
    void un_post_sans_jeton_csrf_repond_403_et_pas_401() throws Exception {
        HttpRequest login = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {"email": "lea@endurance.test", "password": "un-mot-de-passe-solide"}
                        """))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(login, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(403);
    }
}
