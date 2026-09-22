import { screen } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import type { WeeklyStats } from '../api/types'
import { lea, loggedInAs } from '../test/fixtures'
import { renderApp } from '../test/render'
import { server } from '../test/server'

const week: WeeklyStats = {
  weekStart: '2026-09-21',
  weekEnd: '2026-09-27',
  sessions: 3,
  totalMinutes: 135,
  totalDistanceKm: 22.5,
  totalCalories: 1234,
  minutesPerDay: [45, 0, 0, 90, 0, 0, 0],
  minutesPerSport: [
    { sport: 'RUNNING', minutes: 90 },
    { sport: 'YOGA', minutes: 45 },
  ],
  goalMinutes: 150,
  goalPercent: 90,
  streakDays: 3,
}

function openDashboard(stats: WeeklyStats) {
  server.use(loggedInAs(lea), http.get('/api/stats/week', () => HttpResponse.json(stats)))
  return renderApp('/')
}

describe('tableau de bord', () => {
  it('résume la semaine en quatre chiffres', async () => {
    openDashboard(week)

    expect(await screen.findByRole('group', { name: 'Séances' })).toHaveTextContent('3')
    expect(screen.getByRole('group', { name: 'Temps' })).toHaveTextContent('2 h 15')
    expect(screen.getByRole('group', { name: 'Distance' })).toHaveTextContent('22,5 km')
    expect(screen.getByRole('group', { name: 'Calories' })).toHaveTextContent(/1\s234 kcal/)
  })

  it("montre la progression vers l'objectif de la semaine", async () => {
    openDashboard(week)

    const bar = await screen.findByRole('progressbar', { name: 'Objectif de la semaine' })
    expect(bar).toHaveAttribute('aria-valuenow', '90')
    expect(screen.getByText('135 / 150 min')).toBeInTheDocument()
  })

  it("félicite quand l'objectif est atteint, sans que la barre déborde", async () => {
    openDashboard({ ...week, totalMinutes: 180, goalPercent: 120 })

    expect(await screen.findByText('Objectif atteint !')).toBeInTheDocument()
    expect(screen.getByRole('progressbar')).toHaveAttribute('aria-valuenow', '100')
  })

  it('affiche la série de jours consécutifs', async () => {
    openDashboard(week)
    expect(await screen.findByText('3 jours d’affilée')).toBeInTheDocument()
  })

  it('dit quand il n’y a pas de série en cours', async () => {
    openDashboard({ ...week, streakDays: 0 })
    expect(await screen.findByText('Pas de série en cours')).toBeInTheDocument()
  })

  it('dessine une barre par jour, du lundi au dimanche', async () => {
    openDashboard(week)

    expect(await screen.findByLabelText('lundi : 45 min')).toBeInTheDocument()
    expect(screen.getByLabelText('jeudi : 1 h 30')).toBeInTheDocument()
    expect(screen.getByLabelText('dimanche : 0 min')).toBeInTheDocument()
  })

  it('répartit le temps par sport', async () => {
    openDashboard(week)

    expect(await screen.findByRole('listitem', { name: 'Course' })).toHaveTextContent('1 h 30')
    expect(screen.getByRole('listitem', { name: 'Yoga' })).toHaveTextContent('45 min')
  })

  it('encourage à commencer quand la semaine est vide', async () => {
    openDashboard({
      ...week, sessions: 0, totalMinutes: 0, totalDistanceKm: 0, totalCalories: 0,
      minutesPerDay: [0, 0, 0, 0, 0, 0, 0], minutesPerSport: [], goalPercent: 0, streakDays: 0,
    })

    expect(await screen.findByText(/Aucune séance cette semaine/)).toBeInTheDocument()
  })
})
