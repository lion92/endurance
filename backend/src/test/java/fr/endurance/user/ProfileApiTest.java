package fr.endurance.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDate;

import fr.endurance.support.Accounts;
import fr.endurance.support.IntegrationTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@IntegrationTest
class ProfileApiTest {

    @Autowired
    MockMvcTester mvc;

    Cookie lea;

    @BeforeEach
    void setUp() {
        lea = new Accounts(mvc).loggedInAthlete();
    }

    @Test
    void l_athlete_modifie_son_nom_son_poids_et_son_objectif() {
        MvcTestResult updated = updateProfile("Léa M.", "62.5", 200);

        assertThat(updated).hasStatus(HttpStatus.OK);
        assertThat(updated).bodyJson().extractingPath("$.displayName").isEqualTo("Léa M.");
        assertThat(updated).bodyJson().extractingPath("$.weightKg").isEqualTo(62.5);
        assertThat(updated).bodyJson().extractingPath("$.weeklyGoalMinutes").isEqualTo(200);
    }

    @Test
    void le_nouveau_poids_sert_aux_seances_suivantes() {
        updateProfile("Léa", "80", 150);

        assertThat(mvc.post().uri("/api/workouts").cookie(lea).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"sport": "RUNNING", "date": "%s", "durationMinutes": 60, "effort": 6}
                        """.formatted(LocalDate.now())))
                .bodyJson().extractingPath("$.calories").isEqualTo(640);
    }

    @Test
    void un_poids_ou_un_objectif_absurde_est_refuse() {
        MvcTestResult refused = updateProfile("Léa", "8", 0);

        assertThat(refused).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(refused).bodyJson().extractingPath("$.errors.weightKg").isEqualTo("Entre 25 et 300 kg");
        assertThat(refused).bodyJson().extractingPath("$.errors.weeklyGoalMinutes")
                .isEqualTo("Entre 30 et 3000 minutes");
    }

    private MvcTestResult updateProfile(String name, String weight, int goal) {
        return mvc.put().uri("/api/me").cookie(lea).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"displayName": "%s", "weightKg": %s, "weeklyGoalMinutes": %d}
                        """.formatted(name, weight, goal))
                .exchange();
    }
}
