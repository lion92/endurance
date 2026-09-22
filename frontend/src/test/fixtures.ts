import { http, HttpResponse } from 'msw'
import type { User } from '../api/types'

export const lea: User = {
  id: 1,
  email: 'lea@endurance.fr',
  displayName: 'Léa',
  weightKg: 62,
  weeklyGoalMinutes: 150,
}

export const notLoggedIn = http.get('/api/me', () =>
  HttpResponse.json({ status: 401, detail: 'Non authentifié' }, { status: 401 }),
)

export const loggedInAs = (user: User) => http.get('/api/me', () => HttpResponse.json(user))

export const emptyWeek = http.get('/api/stats/week', () =>
  HttpResponse.json({
    weekStart: '2026-09-21', weekEnd: '2026-09-27', sessions: 0, totalMinutes: 0,
    totalDistanceKm: 0, totalCalories: 0, minutesPerDay: [0, 0, 0, 0, 0, 0, 0],
    minutesPerSport: [], goalMinutes: 150, goalPercent: 0, streakDays: 0,
  }),
)
