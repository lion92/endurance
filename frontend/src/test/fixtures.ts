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
