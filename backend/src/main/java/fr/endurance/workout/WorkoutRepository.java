package fr.endurance.workout;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    /** Toujours filtrer par propriétaire : c'est ICI que se joue l'isolement entre utilisateurs. */
    Optional<Workout> findByIdAndUserId(Long id, Long userId);

    List<Workout> findByUserIdAndDateBetween(Long userId, LocalDate from, LocalDate to);

    Page<Workout> findByUserIdOrderByDateDescIdDesc(Long userId, Pageable pageable);
}
