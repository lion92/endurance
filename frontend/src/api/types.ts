// Le miroir TypeScript des records Java renvoyés par l'API.

export type Sport = 'RUNNING' | 'CYCLING' | 'SWIMMING' | 'STRENGTH' | 'WALKING' | 'YOGA'

export interface User {
  id: number
  email: string
  displayName: string
  weightKg: number
  weeklyGoalMinutes: number
}

export interface WorkoutDetails {
  sport: Sport
  date: string // AAAA-MM-JJ
  durationMinutes: number
  distanceKm: number | null
  effort: number
  notes: string | null
}

export interface Workout extends WorkoutDetails {
  id: number
  calories: number
}

export interface Page<T> {
  items: T[]
  page: number
  size: number
  totalItems: number
  totalPages: number
}

export interface WeeklyStats {
  weekStart: string
  weekEnd: string
  sessions: number
  totalMinutes: number
  totalDistanceKm: number
  totalCalories: number
  minutesPerDay: number[]
  minutesPerSport: { sport: Sport; minutes: number }[]
  goalMinutes: number
  goalPercent: number
  streakDays: number
}
