package fr.endurance.workout;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Duration;

import org.junit.jupiter.api.Test;

class CalorieEstimatorTest {

    @Test
    void une_heure_de_course_a_70_kg_brule_560_kcal() {
        // MET de la course = 8,0 → 8,0 × 70 kg × 1 h
        assertThat(CalorieEstimator.estimate(Sport.RUNNING, kg("70"), Duration.ofHours(1)))
                .isEqualTo(560);
    }

    @Test
    void la_duree_compte_en_fraction_d_heure() {
        // 7,5 × 80 kg × 0,5 h
        assertThat(CalorieEstimator.estimate(Sport.CYCLING, kg("80"), Duration.ofMinutes(30)))
                .isEqualTo(300);
    }

    @Test
    void le_resultat_est_arrondi_a_la_calorie_la_plus_proche() {
        // 3,5 × 62,5 kg × 0,75 h = 164,06…
        assertThat(CalorieEstimator.estimate(Sport.WALKING, kg("62.5"), Duration.ofMinutes(45)))
                .isEqualTo(164);
    }

    @Test
    void a_duree_egale_un_sport_plus_intense_brule_plus() {
        int yoga = CalorieEstimator.estimate(Sport.YOGA, kg("70"), Duration.ofHours(1));
        int swimming = CalorieEstimator.estimate(Sport.SWIMMING, kg("70"), Duration.ofHours(1));

        assertThat(swimming).isGreaterThan(yoga);
    }

    private static BigDecimal kg(String value) {
        return new BigDecimal(value);
    }
}
