package fr.endurance.workout;

import java.math.BigDecimal;

/**
 * Chaque sport porte son MET (« équivalent métabolique ») : combien de fois la dépense au repos.
 * Valeurs « effort général » du Compendium of Physical Activities (Ainsworth et al., 2011).
 */
public enum Sport {
    RUNNING("8.0"),
    CYCLING("7.5"),
    SWIMMING("5.8"),
    STRENGTH("3.5"),
    WALKING("3.5"),
    YOGA("2.5");

    private final BigDecimal met;

    Sport(String met) {
        this.met = new BigDecimal(met);
    }

    public BigDecimal met() {
        return met;
    }
}
