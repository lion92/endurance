package fr.endurance.workout;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WorkoutResponse(Long id, Sport sport, LocalDate date, int durationMinutes,
                              BigDecimal distanceKm, int effort, int calories, String notes) {

    public static WorkoutResponse from(Workout workout) {
        return new WorkoutResponse(workout.getId(), workout.getSport(), workout.getDate(),
                workout.getDurationMinutes(), workout.getDistanceKm(), workout.getEffort(),
                workout.getCalories(), workout.getNotes());
    }
}
