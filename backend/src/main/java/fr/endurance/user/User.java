package fr.endurance.user;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    /** 150 minutes d'activité modérée par semaine : la recommandation de l'OMS pour un adulte. */
    public static final int DEFAULT_WEEKLY_GOAL_MINUTES = 150;
    public static final BigDecimal DEFAULT_WEIGHT_KG = new BigDecimal("70.0");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "weight_kg", nullable = false)
    private BigDecimal weightKg = DEFAULT_WEIGHT_KG;

    @Column(name = "weekly_goal_minutes", nullable = false)
    private int weeklyGoalMinutes = DEFAULT_WEEKLY_GOAL_MINUTES;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected User() {
        // exigé par JPA
    }

    public User(String email, String passwordHash, String displayName) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public int getWeeklyGoalMinutes() {
        return weeklyGoalMinutes;
    }
}
