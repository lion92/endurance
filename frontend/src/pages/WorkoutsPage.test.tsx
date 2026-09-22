import { screen, within } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { fakeWorkoutsApi, run, yoga } from '../test/fakeWorkouts'
import { lea, loggedInAs } from '../test/fixtures'
import { renderApp } from '../test/render'
import { server } from '../test/server'

function openWorkouts(initial = [run, yoga]) {
  const api = fakeWorkoutsApi(initial)
  server.use(loggedInAs(lea), ...api.handlers)
  return { ...renderApp('/seances'), api }
}

describe('page Séances', () => {
  it('liste les séances avec leurs chiffres, en français', async () => {
    openWorkouts()

    const row = await screen.findByRole('row', { name: /Course/ })
    expect(row).toHaveTextContent('1 h')
    expect(row).toHaveTextContent('10,5 km')
    expect(row).toHaveTextContent('560 kcal')
    expect(screen.getByRole('row', { name: /Yoga/ })).toHaveTextContent('—')
  })

  it("invite à enregistrer une première séance quand il n'y en a aucune", async () => {
    openWorkouts([])

    expect(await screen.findByText('Aucune séance pour l’instant.')).toBeInTheDocument()
  })

  it('enregistre une nouvelle séance et la montre aussitôt', async () => {
    const { user, api } = openWorkouts([])

    await user.click(await screen.findByRole('button', { name: 'Nouvelle séance' }))
    await user.selectOptions(screen.getByLabelText('Sport'), 'Vélo')
    await user.clear(screen.getByLabelText('Date'))
    await user.type(screen.getByLabelText('Date'), '2026-09-19')
    await user.clear(screen.getByLabelText('Durée (min)'))
    await user.type(screen.getByLabelText('Durée (min)'), '90')
    await user.type(screen.getByLabelText('Distance (km)'), '42,5')
    await user.click(screen.getByRole('button', { name: 'Enregistrer' }))

    expect(await screen.findByRole('row', { name: /Vélo/ })).toHaveTextContent('788 kcal')
    expect(api.received[0].body).toMatchObject({
      sport: 'CYCLING', date: '2026-09-19', durationMinutes: 90, distanceKm: 42.5,
    })
  })

  it('affiche sous le champ l’erreur de validation du serveur', async () => {
    const { user } = openWorkouts([])

    await user.click(await screen.findByRole('button', { name: 'Nouvelle séance' }))
    await user.clear(screen.getByLabelText('Durée (min)'))
    await user.type(screen.getByLabelText('Durée (min)'), '0')
    await user.click(screen.getByRole('button', { name: 'Enregistrer' }))

    expect(await screen.findByText('Entre 1 et 600 minutes')).toBeInTheDocument()
  })

  it('modifie une séance existante', async () => {
    const { user, api } = openWorkouts()

    const row = await screen.findByRole('row', { name: /Course/ })
    await user.click(within(row).getByRole('button', { name: 'Modifier' }))
    expect(screen.getByLabelText('Durée (min)')).toHaveValue(60)
    await user.clear(screen.getByLabelText('Durée (min)'))
    await user.type(screen.getByLabelText('Durée (min)'), '30')
    await user.click(screen.getByRole('button', { name: 'Enregistrer' }))

    expect(await screen.findByRole('row', { name: /Course/ })).toHaveTextContent('280 kcal')
    expect(api.received[0].method).toBe('PUT')
  })

  it('demande une confirmation avant de supprimer', async () => {
    const { user } = openWorkouts()

    const row = await screen.findByRole('row', { name: /Yoga/ })
    await user.click(within(row).getByRole('button', { name: 'Supprimer' }))
    expect(screen.getByRole('row', { name: /Yoga/ })).toBeInTheDocument()
    await user.click(within(row).getByRole('button', { name: 'Confirmer' }))

    await expect.poll(() => screen.queryByRole('row', { name: /Yoga/ })).toBeNull()
  })

  it('pagine au-delà de dix séances', async () => {
    const many = Array.from({ length: 12 }, (_, i) => ({
      ...run, id: i + 1, date: `2026-09-${String(i + 1).padStart(2, '0')}`,
    }))
    const { user } = openWorkouts(many)

    expect(await screen.findByText('Page 1 sur 2')).toBeInTheDocument()
    await user.click(screen.getByRole('button', { name: 'Page suivante' }))

    expect(await screen.findByText('Page 2 sur 2')).toBeInTheDocument()
    expect(screen.getAllByRole('row', { name: /Course/ })).toHaveLength(2)
  })
})
