package fr.endurance.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmailsTest {

    @Test
    void un_email_est_ramene_en_minuscules_et_sans_espaces_autour() {
        assertThat(Emails.normalize("  Lea.Martin@Exemple.FR ")).isEqualTo("lea.martin@exemple.fr");
    }

    @Test
    void null_reste_null_pour_laisser_la_validation_repondre() {
        assertThat(Emails.normalize(null)).isNull();
    }
}
