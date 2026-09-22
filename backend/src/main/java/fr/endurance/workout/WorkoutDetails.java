package fr.endurance.workout;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

/** Ce que l'athlète saisit. Les calories n'y sont pas : c'est le serveur qui les calcule. */
public record WorkoutDetails(
        @NotNull(message = "Le sport est obligatoire")
        Sport sport,

        @NotNull(message = "La date est obligatoire")
        @PastOrPresent(message = "Une séance ne peut pas être dans le futur")
        LocalDate date,

        @Min(value = 1, message = "Entre 1 et 600 minutes")
        @Max(value = 600, message = "Entre 1 et 600 minutes")
        int durationMinutes,

        @DecimalMin(value = "0.0", message = "La distance ne peut pas être négative")
        BigDecimal distanceKm,

        @Min(value = 1, message = "Le ressenti va de 1 à 10")
        @Max(value = 10, message = "Le ressenti va de 1 à 10")
        int effort,

        @Size(max = 500, message = "500 caractères maximum")
        String notes) {
}
