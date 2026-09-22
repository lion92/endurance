package fr.endurance.stats;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDate;

import fr.endurance.support.Accounts;
import fr.endurance.support.IntegrationTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

/**
 * Les cas de calendrier (bornes de semaine, séries) sont couverts par WeeklyStatsTest, avec des
 * dates FIXES. Ici on ne vérifie que le branchement, avec des séances d'AUJOURD'HUI : un test qui
 * dépendrait du jour de la semaine où il tourne échouerait un lundi sur sept.
 */
@IntegrationTest
class StatsApiTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void le_tableau_de_bord_additionne_les_seances_de_l_athlete_connecte() {
        Cookie lea = new Accounts(mvc).loggedInAthlete();
        Cookie tom = new Accounts(mvc).loggedInAthlete();
        create(lea, "RUNNING", 60, "10");
        create(lea, "YOGA", 30, null);
        create(tom, "CYCLING", 120, "50");

        MvcTestResult week = mvc.get().uri("/api/stats/week").cookie(lea).exchange();

        assertThat(week).hasStatus(HttpStatus.OK);
        assertThat(week).bodyJson().extractingPath("$.sessions").isEqualTo(2);
        assertThat(week).bodyJson().extractingPath("$.totalMinutes").isEqualTo(90);
        assertThat(week).bodyJson().extractingPath("$.totalCalories").isEqualTo(560 + 88);
        assertThat(week).bodyJson().extractingPath("$.goalMinutes").isEqualTo(150);
        assertThat(week).bodyJson().extractingPath("$.goalPercent").isEqualTo(60);
        assertThat(week).bodyJson().extractingPath("$.streakDays").isEqualTo(1);
    }

    private void create(Cookie athlete, String sport, int minutes, String km) {
        assertThat(mvc.post().uri("/api/workouts").cookie(athlete).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"sport": "%s", "date": "%s", "durationMinutes": %d, "distanceKm": %s, "effort": 5}
                        """.formatted(sport, LocalDate.now(), minutes, km)))
                .hasStatus(HttpStatus.CREATED);
    }
}
