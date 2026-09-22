package fr.endurance.stats;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import fr.endurance.user.ProfileService;
import fr.endurance.workout.Workout;
import fr.endurance.workout.WorkoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatsService {

    /** Au-delà d'un an, une série de jours consécutifs n'est plus comptée : c'est assumé. */
    private static final int HISTORY_DAYS = 366;

    private final WorkoutRepository workouts;
    private final ProfileService profiles;
    private final Clock clock;

    public StatsService(WorkoutRepository workouts, ProfileService profiles, Clock clock) {
        this.workouts = workouts;
        this.profiles = profiles;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public WeeklyStats thisWeek(Long userId) {
        int goal = profiles.get(userId).getWeeklyGoalMinutes();
        LocalDate today = LocalDate.now(clock);
        List<Workout> history = workouts.findByUserIdAndDateBetween(
                userId, today.minusDays(HISTORY_DAYS), today.plusDays(6));
        return WeeklyStats.compute(history, today, goal);
    }
}
