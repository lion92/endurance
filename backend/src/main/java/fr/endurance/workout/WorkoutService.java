package fr.endurance.workout;

import java.math.BigDecimal;

import fr.endurance.common.ResourceNotFoundException;
import fr.endurance.user.ProfileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WorkoutService {

    private final WorkoutRepository workouts;
    private final ProfileService profiles;

    public WorkoutService(WorkoutRepository workouts, ProfileService profiles) {
        this.workouts = workouts;
        this.profiles = profiles;
    }

    public Workout create(Long userId, WorkoutDetails details) {
        return workouts.save(new Workout(userId, details, weightOf(userId)));
    }

    @Transactional(readOnly = true)
    public Page<Workout> list(Long userId, Pageable pageable) {
        return workouts.findByUserIdOrderByDateDescIdDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Workout get(Long userId, Long id) {
        return workouts.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Séance introuvable"));
    }

    public Workout update(Long userId, Long id, WorkoutDetails details) {
        Workout workout = get(userId, id);
        workout.update(details, weightOf(userId));
        return workout;
    }

    public void delete(Long userId, Long id) {
        workouts.delete(get(userId, id));
    }

    private BigDecimal weightOf(Long userId) {
        return profiles.get(userId).getWeightKg();
    }
}
