package fr.endurance.stats;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import fr.endurance.workout.Sport;
import fr.endurance.workout.Workout;
import fr.endurance.workout.WorkoutDetails;
import org.junit.jupiter.api.Test;

class WeeklyStatsTest {

    /** Un jeudi. La semaine qui le contient va du lundi 14 au dimanche 20 septembre 2026. */
    private static final LocalDate THURSDAY = LocalDate.of(2026, 9, 17);
    private static final int GOAL = 150;

    @Test
    void la_semaine_va_du_lundi_au_dimanche() {
        WeeklyStats stats = WeeklyStats.compute(List.of(), THURSDAY, GOAL);

        assertThat(stats.weekStart()).isEqualTo(LocalDate.of(2026, 9, 14));
        assertThat(stats.weekEnd()).isEqualTo(LocalDate.of(2026, 9, 20));
    }

    @Test
    void seules_les_seances_de_la_semaine_comptent_dans_les_totaux() {
        List<Workout> history = List.of(
                run(THURSDAY, 40, "8"),
                run(THURSDAY.minusDays(3), 20, "4"),     // lundi : dans la semaine
                run(THURSDAY.minusDays(4), 90, "18"));   // dimanche d'avant : hors semaine

        WeeklyStats stats = WeeklyStats.compute(history, THURSDAY, GOAL);

        assertThat(stats.sessions()).isEqualTo(2);
        assertThat(stats.totalMinutes()).isEqualTo(60);
        assertThat(stats.totalDistanceKm()).isEqualByComparingTo("12");
    }

    @Test
    void les_minutes_sont_rangees_jour_par_jour_en_commencant_lundi() {
        List<Workout> history = List.of(
                run(THURSDAY.minusDays(3), 20, "4"),
                run(THURSDAY, 40, "8"),
                run(THURSDAY, 15, "3"));

        WeeklyStats stats = WeeklyStats.compute(history, THURSDAY, GOAL);

        assertThat(stats.minutesPerDay()).containsExactly(20, 0, 0, 55, 0, 0, 0);
    }

    @Test
    void la_progression_vers_l_objectif_est_un_pourcentage_arrondi_vers_le_bas() {
        WeeklyStats stats = WeeklyStats.compute(List.of(run(THURSDAY, 100, "20")), THURSDAY, GOAL);

        assertThat(stats.goalMinutes()).isEqualTo(150);
        assertThat(stats.goalPercent()).isEqualTo(66);
    }

    @Test
    void la_serie_compte_les_jours_consecutifs_jusqu_a_aujourd_hui() {
        List<Workout> history = List.of(
                run(THURSDAY, 30, "5"),
                run(THURSDAY.minusDays(1), 30, "5"),
                run(THURSDAY.minusDays(2), 30, "5"),
                run(THURSDAY.minusDays(4), 30, "5"));   // le trou de la veille casse la série

        assertThat(WeeklyStats.compute(history, THURSDAY, GOAL).streakDays()).isEqualTo(3);
    }

    @Test
    void la_serie_survit_tant_que_la_journee_n_est_pas_finie() {
        List<Workout> history = List.of(
                run(THURSDAY.minusDays(1), 30, "5"),
                run(THURSDAY.minusDays(2), 30, "5"));

        assertThat(WeeklyStats.compute(history, THURSDAY, GOAL).streakDays()).isEqualTo(2);
    }

    @Test
    void sans_seance_hier_ni_aujourd_hui_la_serie_est_a_zero() {
        List<Workout> history = List.of(run(THURSDAY.minusDays(2), 30, "5"));

        assertThat(WeeklyStats.compute(history, THURSDAY, GOAL).streakDays()).isZero();
    }

    @Test
    void le_temps_est_reparti_par_sport_du_plus_pratique_au_moins_pratique() {
        List<Workout> history = List.of(
                workout(Sport.YOGA, THURSDAY, 20, null),
                run(THURSDAY, 45, "9"),
                workout(Sport.YOGA, THURSDAY.minusDays(1), 30, null));

        WeeklyStats stats = WeeklyStats.compute(history, THURSDAY, GOAL);

        assertThat(stats.minutesPerSport()).containsExactly(
                new SportMinutes(Sport.YOGA, 50),
                new SportMinutes(Sport.RUNNING, 45));
    }

    private static Workout run(LocalDate date, int minutes, String km) {
        return workout(Sport.RUNNING, date, minutes, km);
    }

    private static Workout workout(Sport sport, LocalDate date, int minutes, String km) {
        BigDecimal distance = km == null ? null : new BigDecimal(km);
        return new Workout(1L, new WorkoutDetails(sport, date, minutes, distance, 5, null),
                new BigDecimal("70"));
    }
}
