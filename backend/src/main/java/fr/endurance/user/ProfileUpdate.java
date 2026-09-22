package fr.endurance.user;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProfileUpdate(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 60, message = "60 caractères maximum")
        String displayName,

        @NotNull(message = "Le poids est obligatoire")
        @DecimalMin(value = "25", message = "Entre 25 et 300 kg")
        @DecimalMax(value = "300", message = "Entre 25 et 300 kg")
        BigDecimal weightKg,

        @Min(value = 30, message = "Entre 30 et 3000 minutes")
        @Max(value = 3000, message = "Entre 30 et 3000 minutes")
        int weeklyGoalMinutes) {
}
