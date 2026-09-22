package fr.endurance.workout;

import static org.assertj.core.api.Assertions.assertThat;
import static fr.endurance.support.Workouts.idOf;
import static fr.endurance.support.Workouts.json;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDate;

import fr.endurance.support.Accounts;
import fr.endurance.support.IntegrationTest;
import fr.endurance.support.Workouts;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@IntegrationTest
class WorkoutApiTest {

    private static final LocalDate TODAY = LocalDate.now();

    @Autowired
    MockMvcTester mvc;

    Cookie lea;

    @BeforeEach
    void setUp() {
        lea = new Accounts(mvc).loggedInAthlete();
    }

    @Test
    void enregistrer_une_seance_calcule_ses_calories() {
        MvcTestResult created = create(lea, json("RUNNING", TODAY, 60, "10.5", 7));

        assertThat(created).hasStatus(HttpStatus.CREATED);
        assertThat(created).hasHeader("Location", "/api/workouts/" + idOf(created));
        assertThat(created).bodyJson().extractingPath("$.calories").isEqualTo(560);
        assertThat(created).bodyJson().extractingPath("$.distanceKm").isEqualTo(10.5);
    }

    @Test
    void la_liste_est_triee_de_la_plus_recente_a_la_plus_ancienne() {
        create(lea, json("WALKING", TODAY.minusDays(3), 30, null, 3));
        create(lea, json("CYCLING", TODAY, 45, "20", 6));
        create(lea, json("YOGA", TODAY.minusDays(1), 60, null, 2));

        assertThat(mvc.get().uri("/api/workouts").cookie(lea))
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .extractingPath("$.items[*].sport").asArray()
                .containsExactly("CYCLING", "YOGA", "WALKING");
    }

    @Test
    void la_liste_est_paginee() {
        for (int day = 0; day < 3; day++) {
            create(lea, json("RUNNING", TODAY.minusDays(day), 30, "5", 5));
        }

        MvcTestResult page = mvc.get().uri("/api/workouts?page=1&size=2").cookie(lea).exchange();

        assertThat(page).bodyJson().extractingPath("$.items.length()").isEqualTo(1);
        assertThat(page).bodyJson().extractingPath("$.totalItems").isEqualTo(3);
        assertThat(page).bodyJson().extractingPath("$.totalPages").isEqualTo(2);
    }

    @Test
    void personne_ne_voit_les_seances_d_un_autre() {
        Cookie tom = new Accounts(mvc).loggedInAthlete();
        long leasRun = idOf(create(lea, json("RUNNING", TODAY, 60, "10", 7)));

        assertThat(mvc.get().uri("/api/workouts").cookie(tom))
                .bodyJson().extractingPath("$.totalItems").isEqualTo(0);
        assertThat(mvc.get().uri("/api/workouts/" + leasRun).cookie(tom))
                .hasStatus(HttpStatus.NOT_FOUND);
        assertThat(mvc.delete().uri("/api/workouts/" + leasRun).cookie(tom).with(csrf()))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void modifier_une_seance_recalcule_ses_calories() {
        long id = idOf(create(lea, json("RUNNING", TODAY, 60, "10", 7)));

        MvcTestResult updated = mvc.put().uri("/api/workouts/" + id).cookie(lea).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("RUNNING", TODAY, 30, "5", 7))
                .exchange();

        assertThat(updated).hasStatus(HttpStatus.OK)
                .bodyJson().extractingPath("$.calories").isEqualTo(280);
    }

    @Test
    void supprimer_une_seance() {
        long id = idOf(create(lea, json("YOGA", TODAY, 60, null, 2)));

        assertThat(mvc.delete().uri("/api/workouts/" + id).cookie(lea).with(csrf()))
                .hasStatus(HttpStatus.NO_CONTENT);
        assertThat(mvc.get().uri("/api/workouts/" + id).cookie(lea))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void une_seance_incoherente_est_refusee_champ_par_champ() {
        MvcTestResult refused = create(lea, json("RUNNING", TODAY.plusDays(1), 0, "-2", 11));

        assertThat(refused).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(refused).bodyJson().extractingPath("$.errors.date")
                .isEqualTo("Une séance ne peut pas être dans le futur");
        assertThat(refused).bodyJson().extractingPath("$.errors.durationMinutes")
                .isEqualTo("Entre 1 et 600 minutes");
        assertThat(refused).bodyJson().extractingPath("$.errors.distanceKm")
                .isEqualTo("La distance ne peut pas être négative");
        assertThat(refused).bodyJson().extractingPath("$.errors.effort")
                .isEqualTo("Le ressenti va de 1 à 10");
    }

    @Test
    void sans_connexion_pas_de_seances() {
        assertThat(mvc.get().uri("/api/workouts")).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    private MvcTestResult create(Cookie athlete, String json) {
        return new Workouts(mvc).create(athlete, json);
    }
}
