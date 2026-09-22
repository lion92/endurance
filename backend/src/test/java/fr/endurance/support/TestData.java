package fr.endurance.support;

import java.util.UUID;

/**
 * Chaque test crée SES données, avec des valeurs uniques : aucun test ne dépend d'un autre,
 * et aucun n'a besoin de vider la base.
 */
public final class TestData {

    private TestData() {
    }

    public static String uniqueEmail() {
        return "athlete-" + UUID.randomUUID() + "@endurance.test";
    }
}
