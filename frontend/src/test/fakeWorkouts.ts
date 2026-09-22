import { http, HttpResponse } from 'msw'
import type { Workout, WorkoutDetails } from '../api/types'

/**
 * Un faux serveur de séances AVEC état : ce qu'on crée apparaît dans la liste suivante.
 * Les calories suivent la même formule que le vrai serveur, à 70 kg.
 */
export function fakeWorkoutsApi(initial: Workout[] = []) {
  const store = [...initial]
  const received: { method: string; body: unknown }[] = []
  const met: Record<string, number> = { RUNNING: 8, CYCLING: 7.5, SWIMMING: 5.8, STRENGTH: 3.5, WALKING: 3.5, YOGA: 2.5 }
  const withCalories = (id: number, d: WorkoutDetails): Workout => ({
    ...d,
    id,
    calories: Math.round((met[d.sport] * 70 * d.durationMinutes) / 60),
  })

  const handlers = [
    http.get('/api/workouts', ({ request }) => {
      const url = new URL(request.url)
      const page = Number(url.searchParams.get('page') ?? 0)
      const size = Number(url.searchParams.get('size') ?? 10)
      const sorted = [...store].sort((a, b) => b.date.localeCompare(a.date) || b.id - a.id)
      return HttpResponse.json({
        items: sorted.slice(page * size, page * size + size),
        page,
        size,
        totalItems: store.length,
        totalPages: Math.ceil(store.length / size),
      })
    }),
    http.post('/api/workouts', async ({ request }) => {
      const body = (await request.json()) as WorkoutDetails
      received.push({ method: 'POST', body })
      if (body.durationMinutes < 1) {
        return HttpResponse.json(
          { status: 400, detail: 'Certains champs sont invalides', errors: { durationMinutes: 'Entre 1 et 600 minutes' } },
          { status: 400 },
        )
      }
      const created = withCalories(Math.max(0, ...store.map((w) => w.id)) + 1, body)
      store.push(created)
      return HttpResponse.json(created, { status: 201 })
    }),
    http.put('/api/workouts/:id', async ({ params, request }) => {
      const body = (await request.json()) as WorkoutDetails
      received.push({ method: 'PUT', body })
      const index = store.findIndex((w) => w.id === Number(params.id))
      store[index] = withCalories(Number(params.id), body)
      return HttpResponse.json(store[index])
    }),
    http.delete('/api/workouts/:id', ({ params }) => {
      store.splice(store.findIndex((w) => w.id === Number(params.id)), 1)
      return new HttpResponse(null, { status: 204 })
    }),
  ]
  return { handlers, received }
}

export const run: Workout = {
  id: 1, sport: 'RUNNING', date: '2026-09-21', durationMinutes: 60, distanceKm: 10.5,
  effort: 7, calories: 560, notes: 'Sortie longue au bord du canal',
}

export const yoga: Workout = {
  id: 2, sport: 'YOGA', date: '2026-09-20', durationMinutes: 45, distanceKm: null,
  effort: 3, calories: 131, notes: null,
}
