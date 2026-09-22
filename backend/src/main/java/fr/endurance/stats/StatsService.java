package fr.endurance.stats;

import java.time.Clock;
import java.time.LocalDate;

import fr.endurance.user.User;
import fr.endurance.user.UserRepository;
import fr.endurance.user.UnknownUserException;
import fr.endurance.workout.WorkoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatsService {

    /** Au-delà d'un an, une série de jours consécutifs n'est plus comptée : c'est assumé. */
    private static final int HISTORY_DAYS = 366;

    private final WorkoutRepository workouts;
    private final UserRepository users;
    private final Clock clock;

    public StatsService(WorkoutRepository workouts, UserRepository users, Clock clock) {
        this.workouts = workouts;
        this.users = users;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public WeeklyStats thisWeek(Long userId) {
        User user = users.findById(userId).orElseThrow(UnknownUserException::new);
        LocalDate today = LocalDate.now(clock);
        var history = workouts.findByUserIdAndDateBetween(userId, today.minusDays(HISTORY_DAYS), today.plusDays(6));
        return WeeklyStats.compute(history, today, user.getWeeklyGoalMinutes());
    }
}
