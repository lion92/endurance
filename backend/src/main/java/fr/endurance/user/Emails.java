package fr.endurance.user;

import java.util.Locale;

/** Une adresse, une seule forme : sinon « Lea@x.fr » et « lea@x.fr » seraient deux comptes. */
public final class Emails {

    private Emails() {
    }

    public static String normalize(String email) {
        return email == null ? null : email.strip().toLowerCase(Locale.ROOT);
    }
}
