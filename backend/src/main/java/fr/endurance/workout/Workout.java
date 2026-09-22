package fr.endurance.workout;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Une référence par identifiant, pas un lien objet : une séance n'a pas à charger son auteur. */
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sport sport;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "distance_km")
    private BigDecimal distanceKm;

    @Column(nullable = false)
    private int effort;

    /** Calculées à l'enregistrement, avec le poids de CE jour-là : l'historique ne bouge plus. */
    @Column(nullable = false)
    private int calories;

    private String notes;

    protected Workout() {
        // exigé par JPA
    }

    public Workout(Long userId, WorkoutDetails details, BigDecimal weightKg) {
        this.userId = userId;
        apply(details, weightKg);
    }

    public void update(WorkoutDetails details, BigDecimal weightKg) {
        apply(details, weightKg);
    }

    private void apply(WorkoutDetails details, BigDecimal weightKg) {
        this.sport = details.sport();
        this.date = details.date();
        this.durationMinutes = details.durationMinutes();
        this.distanceKm = details.distanceKm();
        this.effort = details.effort();
        this.notes = details.notes();
        this.calories = CalorieEstimator.estimate(sport, weightKg, Duration.ofMinutes(durationMinutes));
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Sport getSport() {
        return sport;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public int getEffort() {
        return effort;
    }

    public int getCalories() {
        return calories;
    }

    public String getNotes() {
        return notes;
    }
}
