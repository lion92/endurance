package fr.endurance.workout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

/** kcal ≈ MET × poids (kg) × durée (h) : la formule usuelle, sans prétention médicale. */
public final class CalorieEstimator {

    private static final BigDecimal SECONDS_PER_HOUR = BigDecimal.valueOf(3600);

    private CalorieEstimator() {
    }

    public static int estimate(Sport sport, BigDecimal weightKg, Duration duration) {
        BigDecimal hours = BigDecimal.valueOf(duration.toSeconds())
                .divide(SECONDS_PER_HOUR, 6, RoundingMode.HALF_UP);
        return sport.met()
                .multiply(weightKg)
                .multiply(hours)
                .setScale(0, RoundingMode.HALF_UP)
                .intValueExact();
    }
}
