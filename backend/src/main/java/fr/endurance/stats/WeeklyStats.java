package fr.endurance.stats;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toSet;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import fr.endurance.workout.Sport;
import fr.endurance.workout.Workout;

/**
 * Le tableau de bord de la semaine. Du calcul PUR : une liste de séances et une date entrent,
 * des chiffres sortent. Aucune base, aucune horloge : c'est ce qui le rend si simple à tester.
 */
public record WeeklyStats(LocalDate weekStart, LocalDate weekEnd, int sessions, int totalMinutes,
                          BigDecimal totalDistanceKm, int totalCalories, List<Integer> minutesPerDay,
                          List<SportMinutes> minutesPerSport, int goalMinutes, int goalPercent,
                          int streakDays) {

    public static WeeklyStats compute(List<Workout> history, LocalDate today, int goalMinutes) {
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);
        List<Workout> week = history.stream()
                .filter(w -> !w.getDate().isBefore(monday) && !w.getDate().isAfter(sunday))
                .toList();

        int totalMinutes = week.stream().mapToInt(Workout::getDurationMinutes).sum();
        return new WeeklyStats(monday, sunday,
                week.size(),
                totalMinutes,
                week.stream().map(Workout::getDistanceKm).filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                week.stream().mapToInt(Workout::getCalories).sum(),
                minutesPerDay(week, monday),
                minutesPerSport(week),
                goalMinutes,
                goalMinutes == 0 ? 0 : totalMinutes * 100 / goalMinutes,
                streak(history, today));
    }

    private static List<Integer> minutesPerDay(List<Workout> week, LocalDate monday) {
        int[] days = new int[7];
        for (Workout workout : week) {
            days[(int) ChronoUnit.DAYS.between(monday, workout.getDate())] += workout.getDurationMinutes();
        }
        return Arrays.stream(days).boxed().toList();
    }

    private static List<SportMinutes> minutesPerSport(List<Workout> week) {
        Map<Sport, Integer> bySport = week.stream()
                .collect(groupingBy(Workout::getSport, summingInt(Workout::getDurationMinutes)));
        return bySport.entrySet().stream()
                .map(entry -> new SportMinutes(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(SportMinutes::minutes).reversed())
                .toList();
    }

    /** Jours consécutifs avec au moins une séance. Tant qu'aujourd'hui n'est pas fini, hier suffit. */
    private static int streak(List<Workout> history, LocalDate today) {
        Set<LocalDate> activeDays = history.stream().map(Workout::getDate).collect(toSet());
        LocalDate day = activeDays.contains(today) ? today : today.minusDays(1);
        int streak = 0;
        while (activeDays.contains(day)) {
            streak++;
            day = day.minusDays(1);
        }
        return streak;
    }
}
