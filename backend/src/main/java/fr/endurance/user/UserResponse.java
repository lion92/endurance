package fr.endurance.user;

import java.math.BigDecimal;

/** Ce que l'API montre d'un utilisateur : jamais son mot de passe, même haché. */
public record UserResponse(Long id, String email, String displayName,
                           BigDecimal weightKg, int weeklyGoalMinutes) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getDisplayName(),
                user.getWeightKg(), user.getWeeklyGoalMinutes());
    }
}
